package com.example.demo.logsample;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UsageLogger {
    public static JsonArray retrieve(Context context,long begin, long end){
        JsonArray jsonArray = new JsonArray();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events = usageStatsManager.queryEvents(begin,end);
        while(events.hasNextEvent()){
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            JsonObject object = new JsonObject();
            switch (event.getEventType()){
                case UsageEvents.Event.ACTIVITY_RESUMED:
                case UsageEvents.Event.ACTIVITY_PAUSED:
                case UsageEvents.Event.ACTIVITY_STOPPED:
                    object.addProperty("type",event.getEventType());
                    object.addProperty("time",event.getTimeStamp());
                    object.addProperty("pkg",event.getPackageName());
                    object.addProperty("cls",event.getClassName());
                    break;
                case UsageEvents.Event.KEYGUARD_HIDDEN:
                case UsageEvents.Event.KEYGUARD_SHOWN:
                case UsageEvents.Event.SCREEN_INTERACTIVE:
                case UsageEvents.Event.SCREEN_NON_INTERACTIVE:
                    object.addProperty("type",event.getEventType());
                    object.addProperty("time",event.getTimeStamp());
                    break;
            }
        }
        return jsonArray;
    }
}
