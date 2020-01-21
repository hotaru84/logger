package com.example.demo.logsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.LruCache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;

public final class BatteryLogger extends BroadcastReceiver {
    private static final int MAX_CACHE_LOGSIZE = 1024 * 1024;
    private final Context context;
    private static LruCache<Long, JsonObject> lruLogCache;
    private static long lastScreenOnTimeMs = 0;
    public BatteryLogger(Context context) {
        this.context = context;
        lruLogCache = new LruCache<>(MAX_CACHE_LOGSIZE);
    }
    public void start(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(this,filter);
        lastScreenOnTimeMs = System.currentTimeMillis();
    }
    public void stop(){
        context.unregisterReceiver(this);
    }
    public static JsonArray retrieve(long begin, long end) {
        JsonArray jsonArray = new JsonArray();
        Map<Long, JsonObject> logs = lruLogCache.snapshot();
        logs.forEach((time, log) -> {
            if(time > begin && time <= end) {
                log.addProperty("time", time);
                jsonArray.add(log);
            }
        });
        return jsonArray;
    }
    public void clearAll() {lruLogCache.evictAll();}

    private JsonObject getObject(Intent batteryStatus){
        JsonObject jsonObject = new JsonObject();
        int plug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)/ 10.0f;
        float voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)/1000;

        jsonObject.addProperty("lv",level * 100 / (float)scale);
        jsonObject.addProperty("v",voltage);
        jsonObject.addProperty("tmp",temp);
        switch (plug){
            case  BatteryManager.BATTERY_PLUGGED_AC: {
                jsonObject.addProperty("plg","AC");
                break;
            }
            case BatteryManager.BATTERY_PLUGGED_USB: {
                jsonObject.addProperty("plg","USB");
            }
        }
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                jsonObject.addProperty("health",health);
                break;
        }
        return jsonObject;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                lastScreenOnTimeMs = System.currentTimeMillis();
                break;
            case Intent.ACTION_SCREEN_OFF: {
                JsonObject object = new JsonObject();
                object.addProperty("type", intent.getAction().replace("android.intent.action.", ""));
                object.addProperty("time",System.currentTimeMillis() - lastScreenOnTimeMs);
                lruLogCache.put(System.currentTimeMillis(), object);
                break;
            }
            case Intent.ACTION_POWER_CONNECTED:
            case Intent.ACTION_POWER_DISCONNECTED:
            case Intent.ACTION_BATTERY_CHANGED:
            case Intent.ACTION_BATTERY_OKAY:
            case Intent.ACTION_BATTERY_LOW: {
                JsonObject object = getObject(intent);
                object.addProperty("type", intent.getAction().replace("android.intent.action.", ""));
                lruLogCache.put(System.currentTimeMillis(), object);
                break;
            }
        }
    }
}
