package com.example.demo.logsample;

import android.content.Intent;
import android.os.BatteryManager;

public final class BatteryUtils {
    public static String getStatus(Intent batteryStatus){
        StringBuilder status = new StringBuilder();
        int plug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)/ 10.0f;
        float voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)/1000;

        status.append("Lv:" + level * 100 / (float)scale + ", temp:" + temp + ", vol: " + voltage +",");
        switch (plug){
            case  BatteryManager.BATTERY_PLUGGED_AC: {
                status.append("AC Plug,");
                break;
            }
            case BatteryManager.BATTERY_PLUGGED_USB: {
                status.append("USB Plug,");
            }
        }
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                status.append("health(" + health + "),");
                break;
        }
        return status.toString();
    }
}
