package com.example.demo.logsample;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class UsageLogger {
    private static UsageStatsManager usageStatsManager;
    public UsageLogger(Context context){
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }
    public void start(){}
    public void stop(){}
    public static JsonObject retrieve(long begin, long end){
        JsonObject rootObject = new JsonObject();
        List pkgList = new ArrayList();
        JsonArray jsonArray = new JsonArray();
        if(usageStatsManager == null) return rootObject;
        UsageEvents events = usageStatsManager.queryEvents(begin,end);
        while(events.hasNextEvent()){
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            JsonObject object = new JsonObject();
            switch (event.getEventType()){
                case UsageEvents.Event.ACTIVITY_RESUMED:
                case UsageEvents.Event.ACTIVITY_PAUSED:
                case UsageEvents.Event.ACTIVITY_STOPPED:
                    int id = pkgList.indexOf(event.getPackageName());
                    if(id < 0) {
                        pkgList.add(event.getPackageName());
                        id = pkgList.size() - 1;
                    }
                    object.addProperty("type",getType(event.getEventType()));
                    object.addProperty("time",event.getTimeStamp());
                    object.addProperty("pkg",id);
                    object.addProperty("cls",event.getClassName());
                    jsonArray.add(object);
                    break;
                case UsageEvents.Event.KEYGUARD_HIDDEN:
                case UsageEvents.Event.KEYGUARD_SHOWN:
                case UsageEvents.Event.SCREEN_INTERACTIVE:
                case UsageEvents.Event.SCREEN_NON_INTERACTIVE:
                    object.addProperty("type",getType(event.getEventType()));
                    object.addProperty("time",event.getTimeStamp());
                    jsonArray.add(object);
                    break;
            }
            //Log.d("@@","type" + event.getEventType() );
        }
        rootObject.add("pkg", new JsonParser().parse(pkgList.toString()));
        rootObject.add("log",jsonArray);
        return rootObject;
    }
    private static String getType (int type) {
        switch (type) {
            case UsageEvents.Event.ACTIVITY_RESUMED:
                return "ACTIVITY_RESUMED";
            case UsageEvents.Event.ACTIVITY_PAUSED:
                return "ACTIVITY_PAUSED";
            case UsageEvents.Event.ACTIVITY_STOPPED:
                return "ACTIVITY_STOPPED";
            case UsageEvents.Event.KEYGUARD_HIDDEN:
                return "KEYGUARD_HIDDEN";
            case UsageEvents.Event.KEYGUARD_SHOWN:
                return "KEYGUARD_SHOWN";
            case UsageEvents.Event.SCREEN_INTERACTIVE:
                return "SCREEN_INTERACTIVE";
            case UsageEvents.Event.SCREEN_NON_INTERACTIVE:
                return "SCREEN_NON_INTERACTIVE";
        }
        return "";
    }
}
