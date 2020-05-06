package com.example.demo.logsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;

import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Type;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class UsageTimeLogger extends BroadcastReceiver implements SensorEventListener {
    private final Context context;
    private final Handler handler = new Handler();
    private Long lastStateChangeSecond;
    private PowerManager powerManager;
    private SensorManager sensorManager;
    private boolean isActive;
    private boolean isMoving;
    private String nowState;

    public UsageTimeLogger(Context context) {
        this.context = context;
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
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
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(this,filter);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        isActive = powerManager.isInteractive();
        isMoving = false;
        lastStateChangeSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        nowState = Type.ACTIVE;
        updateActiveState();
    }
    public void stop(){
        updateActiveState();
        sensorManager.unregisterListener(this);
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                isActive = true;
                updateActiveState();
                break;
            case Intent.ACTION_SCREEN_OFF:
                isActive = false;
                updateActiveState();
                break;
            case Intent.ACTION_BATTERY_CHANGED:
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        handler.removeCallbacks(stepCheck);
        handler.postDelayed(stepCheck,5 * 1000);
        isMoving = true;
        updateActiveState();
    }

    private final Runnable stepCheck = () -> {
        isMoving = false;
        updateActiveState();
    };
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    private void updateActiveState() {
        Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        LogRepository.getInstance().insertStats(nowState,now - lastStateChangeSecond);
        if(isActive) {
            nowState = isMoving ? Type.MOVE_ACTIVE : Type.ACTIVE;
        } else {
            nowState = isMoving ? Type.MOVE_INACTIVE : Type.INACTIVE;
        }
        lastStateChangeSecond = now;
    }
}
