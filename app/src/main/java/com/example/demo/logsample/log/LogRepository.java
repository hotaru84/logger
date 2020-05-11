package com.example.demo.logsample.log;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private MutableLiveData<LocalDate> observableDate = new MutableLiveData<>();
    private static LogRepository repository;
    private SupportSQLiteOpenHelper sqLiteOpenHelper;

    private LogRepository() { }
    public void init(Application application){
        if(logDao != null) return;
        LogRoomDb logRoomDb = LogRoomDb.getDb(application);
        sqLiteOpenHelper = logRoomDb.getOpenHelper();
        logDao = logRoomDb.logDao();
        observableDate.setValue(LocalDate.now());
    }
    public static LogRepository getInstance() {
        if(repository == null) repository = new LogRepository();
        return repository;
    }
    public void setQueryDate(LocalDate d) {
        observableDate.postValue(d);}
    public LiveData<LocalDate> observeDate(){
        return observableDate;
    }
    public LiveData<Long> observeStats(String type) {
        LocalDate date = observableDate.getValue();
        Long startOfToday = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        Long endOfToday = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        return logDao.queryStatsValue(startOfToday,endOfToday,type);
    }
    public Long getStatsValue( String type){
        LocalDate date = observableDate.getValue();
        Long startOfToday = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        Long endOfToday = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
        Future<Long> future = LogRoomDb.dbWriteExecutor.submit(() ->
                logDao.queryStats(startOfToday, endOfToday, type).stream().mapToLong(stats -> stats.getValue()).sum());
        try {
            if(future == null) return 0L;
            return future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0L;
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
