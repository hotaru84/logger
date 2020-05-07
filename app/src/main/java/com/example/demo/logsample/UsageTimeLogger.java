package com.example.demo.logsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;

import androidx.annotation.NonNull;

import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Type;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class UsageTimeLogger implements SensorEventListener {
    private final Context context;
    private final Handler handler = new Handler();
    private Long lastStateChangeSecond;
    private Long lastNetworkChangeSecond;
    private PowerManager powerManager;
    private SensorManager sensorManager;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private boolean isActive;
    private boolean isMoving;
    private String powerState;
    private String networkState;

    public UsageTimeLogger(Context context) {
        this.context = context;
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
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
        context.registerReceiver(powerStateMonitor,filter);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        isActive = powerManager.isInteractive();
        isMoving = false;
        lastStateChangeSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        updateActiveState();
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
        updateNetworkState();
    }
    public void stop(){
        updateActiveState();
        updateNetworkState();
        sensorManager.unregisterListener(this);
        context.unregisterReceiver(powerStateMonitor);
        context.unregisterReceiver(wifiSignalMonitor);
        connectivityManager.unregisterNetworkCallback(networkCallback);
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
        if(powerState != null) {
            LogRepository.getInstance().insertStats(powerState, now - lastStateChangeSecond);
        }
        if(isActive) {
            powerState = isMoving ? Type.MOVE_ACTIVE : Type.ACTIVE;
        } else {
            powerState = isMoving ? Type.MOVE_INACTIVE : Type.INACTIVE;
        }
        lastStateChangeSecond = now;
    }
    private BroadcastReceiver powerStateMonitor = new BroadcastReceiver() {
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
    };
    private void updateNetworkState() {
        Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if(networkState != null){
            LogRepository.getInstance().insertStats(networkState, now - lastNetworkChangeSecond);
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        if(info.getSupplicantState().equals(SupplicantState.DISCONNECTED)) {
            networkState = Type.WIFI_LOST;
        } else {
            int level = WifiManager.calculateSignalLevel(info.getRssi(), 3);
            switch (level) {
                case 1:
                    networkState = Type.WIFI_MID;
                    break;
                case 2:
                    networkState = Type.WIFI_GOOD;
                    break;
                default:
                    networkState = Type.WIFI_LOW;
                    break;
            }
        }
        lastNetworkChangeSecond = now;
    }
    private BroadcastReceiver wifiSignalMonitor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNetworkState();
        }
    };
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            context.registerReceiver(wifiSignalMonitor,filter);
            updateNetworkState();
        }
        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            context.unregisterReceiver(wifiSignalMonitor);
            updateNetworkState();
        }
        @Override
        public void onUnavailable() {
            super.onUnavailable();
            context.unregisterReceiver(wifiSignalMonitor);
            updateNetworkState();
        }
    };
}
