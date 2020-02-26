package com.example.demo.logsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.ArrayMap;
import android.util.Log;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.BiConsumer;

public final class BatteryLogger extends BroadcastReceiver {
    private final Context context;
    private LocalDateTime lastScreenOnTime;
    private LogRepository logRepository;
    Map<LocalDateTime,Long> logs = new ArrayMap<>();
    public BatteryLogger(Context context,LogRepository repository) {
        this.context = context;
        logRepository = repository;
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
        lastScreenOnTime = LocalDateTime.now();
    }
    public void stop(){
        context.unregisterReceiver(this);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                lastScreenOnTime = LocalDateTime.now();
                break;
            case Intent.ACTION_SCREEN_OFF:
                lapScreenOnTime();
                break;
            case Intent.ACTION_BATTERY_CHANGED:
                //lapScreenOnTime();
                break;
        }
    }
    public void lapScreenOnTime() {
        LocalDateTime lapTime = LocalDateTime.now();
        LocalDateTime hour = startOfHour(lapTime);
        LocalDateTime lastHour = startOfHour(lastScreenOnTime);
        while(hour.isAfter(lastHour) || hour.isEqual(lastHour)){
            Long seconds = Duration.between(
                    hour.isAfter(lastScreenOnTime)?hour:lastScreenOnTime,
                    lapTime
            ).getSeconds();
            Long past = logs.get(hour);
            if(past == null) logs.put(hour,seconds);
            else logs.put(hour,past + seconds);
            lapTime = hour;
            hour = hour.minusHours(1);
        }
        logs.forEach((d, v) -> {
            Log.d("@@@",d.format(DateTimeFormatter.ofPattern("YY-MM-DD HH")) + "," + v);
        });
    }
    private LocalDateTime startOfHour(LocalDateTime t) {
        return LocalDateTime.of(t.getYear(),t.getMonth(),t.getDayOfMonth(),t.getHour(),0);
    }
}
