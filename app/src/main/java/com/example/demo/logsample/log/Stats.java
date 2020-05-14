package com.example.demo.logsample.log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.demo.logsample.R;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "stats_table", primaryKeys = {"time","type"})
public class Stats {
    @NonNull
    @ColumnInfo(name = "time")
    private Long time;
    @NonNull
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "value")
    private Long value;

    public Stats(Long time, String type, Long value) {
        this.time = time;
        this.value = value;
        this.type = type;
    }
    public JsonObject getJsonObject(){
        JsonObject obj = new JsonObject();
        obj.addProperty("time",time);
        obj.addProperty("value",value);
        obj.addProperty("type",type);
        return obj;
    }

    public Long getTime() {return time;}

    public Long getValue() { return value; }

    public String getType() { return type; }
    public String getValueText() { return  value.toString();}
}
