package com.example.demo.logsample.log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
    public String getFormattedTime(){
        LocalDateTime d = LocalDateTime.ofEpochSecond(time,0, ZoneOffset.UTC);
        return d.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
