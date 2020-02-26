package com.example.demo.logsample;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "stats_table", primaryKeys = {"date","hour","type"})
public class Stats {
    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "hour")
    private int hour;
    @NonNull
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "value")
    private Long value;

    public Stats(String date, int hour, String type, Long value) {
        //date = time.format(DateTimeFormatter.ofPattern("YYYY-MM-DD"));
        this.date = date;
        this.hour = hour;
        this.value = value;
        this.type = type;
    }
    public JsonObject getJsonObject(){
        JsonObject obj = new JsonObject();
        obj.addProperty("date",date);
        obj.addProperty("hour",hour);
        obj.addProperty("value",value);
        obj.addProperty("type",type);
        return obj;
    }

    public String getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public Long getValue() { return value; }

    public String getType() { return type; }
}
