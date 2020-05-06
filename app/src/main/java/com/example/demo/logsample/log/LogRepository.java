package com.example.demo.logsample.log;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class LogRepository {
    private LogDao logDao;
    private static LogRepository repository;

    private LogRepository() { }
    public void init(Application application){
        if(logDao != null) return;
        LogRoomDb logRoomDb = LogRoomDb.getDb(application);
        logDao = logRoomDb.logDao();
    }
    public static LogRepository getInstance() {
        if(repository == null) repository = new LogRepository();
        return repository;
    }
    public LiveData<Long> observeStats(LocalDate date, String type) {
        Long startOfToday = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        Long endOfToday = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        return logDao.queryStatsValue(startOfToday,endOfToday,type);
    }
    public Future<Long> getStatsValue(LocalDate date, String type){
        Long startOfToday = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        Long endOfToday = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        return  LogRoomDb.dbWriteExecutor.submit(() ->
                logDao.queryStats(startOfToday, endOfToday, type).stream().mapToLong(stats -> stats.getValue()).sum());
    }
    public void insertLog(String type, String data) {
        Long time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000;
         LogRoomDb.dbWriteExecutor.execute(()->{
            logDao.insert(new Log(time,type,data));
        });
    }
    public void insertStats(String type, Long value) {
        Long startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        Long endOfToday = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        LogRoomDb.dbWriteExecutor.execute(()->{
            List<Stats> stats = logDao.queryStats(startOfToday,endOfToday,type);
            Long finalValue = stats.size()> 0 ? value + stats.get(0).getValue() : value;
            logDao.insert(new Stats(startOfToday, type, finalValue));
        });
    }
    public void deleteAll() {
        LogRoomDb.dbWriteExecutor.execute(()->{
            logDao.deleteAllLog();
            logDao.deleteAllStats();
        });
    }
}
