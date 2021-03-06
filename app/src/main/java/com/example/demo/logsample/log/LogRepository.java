package com.example.demo.logsample.log;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LogRepository {
    private LogDao logDao;
    private static LogRepository repository;
    private SupportSQLiteOpenHelper sqLiteOpenHelper;

    private LogRepository() { }
    public void init(Application application){
        if(logDao != null) return;
        LogRoomDb logRoomDb = LogRoomDb.getDb(application);
        sqLiteOpenHelper = logRoomDb.getOpenHelper();
        logDao = logRoomDb.logDao();
    }
    public static LogRepository getInstance() {
        if(repository == null) repository = new LogRepository();
        return repository;
    }
    public LiveData<List<Log>> getLog(LocalDate date) {
        Long startOfToday = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        Long endOfToday = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        return logDao.queryLog(startOfToday,endOfToday);
    }
    public LiveData<Stats> getStats(LocalDate date, String type) {
        Long startOfToday = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        Long endOfToday = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        return logDao.queryStats(startOfToday,endOfToday,type);
    }
    public void insertLog(String type, String data) {
        Long time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
         LogRoomDb.dbWriteExecutor.execute(()->{
            logDao.insert(new Log(time,type,data));
        });
    }
    public void insertStats(String type, Long value) {
        Long startOfToday = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        Long endOfToday = LocalDate.now().plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        LogRoomDb.dbWriteExecutor.execute(()->{
            Long stats = logDao.queryStatsValue(startOfToday,endOfToday,type);
            Long finalValue = stats != null ? value + stats : value;
            logDao.insert(new Stats(startOfToday, type, finalValue));
        });
    }
    public void deleteAll() {
        LogRoomDb.dbWriteExecutor.execute(()->{
            logDao.deleteAllLog();
            logDao.deleteAllStats();
        });
    }
    public long getDatabaseSize() {
        File f = new File(sqLiteOpenHelper.getReadableDatabase().getPath());
        return browseFiles(f);
    }
    private long browseFiles(File dir) {
        long dirSize = 0;
        for (File f: dir.listFiles()) {
            dirSize += f.length();
            if (f.isDirectory()) {
                dirSize += browseFiles(f);
            }
        }
        return dirSize;
    }
}
