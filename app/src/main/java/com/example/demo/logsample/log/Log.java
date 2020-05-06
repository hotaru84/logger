package com.example.demo.logsample.log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Entity(tableName = "log_table")
public class Log {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "time")
    private Long time;
    @NonNull
    @ColumnInfo(name = "type")
    private String type;
    @NonNull
    @ColumnInfo(name = "data")
    private String data;
    public Log(Long time, String type, String data){ this.time = time; this.type = type; this.data = data;}
    @NonNull
    public Long getTime() {
        return time;
    }
    public String getType() {return type;}
    @NonNull
    public String getData() {
        return data;
    }
    public JsonObject getJsonObject(){
        JsonObject obj = new JsonObject();
        obj.addProperty("time",time);
        obj.addProperty("data",data);
        return obj;
    }
}
