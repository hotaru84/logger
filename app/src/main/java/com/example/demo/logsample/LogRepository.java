package com.example.demo.logsample;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.gson.JsonArray;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class LogRepository {
    private LogDao logDao;
    private LiveData<List<Log>>  allLogs;
    private LiveData<List<Stats>> allStats;

    LogRepository(Application application) {
        LogRoomDb logRoomDb = LogRoomDb.getDb(application);
        logDao = logRoomDb.logDao();
        allLogs = logDao.getLogs();
        allStats = logDao.getStats();
    }
    LiveData<List<Log>> getAllLogs(){
        return allLogs;
    }
    LiveData<List<Stats>> getAllStats() {return allStats;}
    JsonArray getLogList() {
        JsonArray array = new JsonArray();
        logDao.getLogList().forEach(log -> {
            array.add(log.getJsonObject());
        });
        return array;
    }
    void insert(Log log) {
        LogRoomDb.dbWriteExecutor.execute(()->{
            logDao.insert(log);
        });
    }
    void insert(Stats log) {
        LogRoomDb.dbWriteExecutor.execute(()->{
            logDao.insert(log);
        });
    }
    Future<Long> queryValue(LocalDateTime date, String type){
        return LogRoomDb.dbWriteExecutor.submit(() -> {
            String day = date.format(DateTimeFormatter.ofPattern("YYYY-MM-DD"));
            int hour = date.getHour();
            return logDao.queryStats(day, hour, type).getValue();
        });
    }
    void deleteAll() {
        LogRoomDb.dbWriteExecutor.execute(()->{
            logDao.deleteAll();
        });
    }
}
