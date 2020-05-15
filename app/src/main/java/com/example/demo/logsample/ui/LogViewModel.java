package com.example.demo.logsample.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.demo.logsample.log.Log;
import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Stats;
import com.example.demo.logsample.log.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogViewModel extends AndroidViewModel {
    private MutableLiveData<LocalDate> queryDate = new MutableLiveData<>();
    private MediatorLiveData<Long> elapsedTime = new MediatorLiveData<>();
    private MediatorLiveData<Long> activityTime = new MediatorLiveData<>();
    private LiveData<Stats> dbActiveStats;
    private LiveData<Stats> dbInactiveStats;
    private LiveData<Stats> dbActiveMoveStats;
    private LiveData<Stats> dbInactiveMoveStats;
    private LiveData<List<Log>> logs;
    private Long act = 0L;
    private Long inact = 0L;
    private Long moveAct = 0L;
    private Long moveInact = 0L;

    public LogViewModel(@NonNull Application application) {
        super(application);
        LogRepository.getInstance().init(application);
        dbActiveStats = queryStats(Type.ACTIVE);
        dbInactiveStats = queryStats(Type.INACTIVE);
        dbActiveMoveStats = queryStats(Type.MOVE_ACTIVE);
        dbInactiveMoveStats = queryStats(Type.MOVE_INACTIVE);

        elapsedTime.addSource(dbActiveStats, elapsedTimeObserver);
        elapsedTime.addSource(dbInactiveStats,elapsedTimeObserver);
        elapsedTime.addSource(dbActiveMoveStats,elapsedTimeObserver);
        elapsedTime.addSource(dbInactiveMoveStats,elapsedTimeObserver);

        logs = Transformations.switchMap(queryDate,
                q -> LogRepository.getInstance().getLog(q));
    }
    public void setQueryDate(LocalDate date) {
        queryDate.setValue(date);
    }
    public LiveData<String> getQueryDate() {
        return Transformations.map(queryDate, d->d.format(DateTimeFormatter.ISO_DATE));
    }
    public LiveData<List<Log>> queryLogList() {
        return logs;
    }
    public LiveData<Integer> getActivityTime() {
        return Transformations.map(activityTime,l->l.intValue());
    }
    public LiveData<Integer> getElapsedTime() {
        return Transformations.map(elapsedTime,l->l.intValue());
    }
    public LiveData<Integer> getDbActiveStats() {
        return getValue(dbActiveStats);
    }

    public LiveData<Integer> getDbActiveMoveStats() {
        return getValue(dbActiveMoveStats);
    }

    public LiveData<Integer> getDbInactiveMoveStats() {
        return getValue(dbInactiveMoveStats);
    }
    private LiveData<Integer> getValue(LiveData<Stats> stats) {
        return Transformations.map(stats,s->{
            if(s == null) return 0;
            if(s.getValue() == null) return 0;
            return s.getValue().intValue();
        });
    }
    private LiveData<Stats> queryStats(String type) {
        return Transformations.switchMap(queryDate,
                q-> LogRepository.getInstance().getStats(q, type));
    }
    private Observer<Stats> elapsedTimeObserver = (stats)->{
        if(stats == null) return;
        switch (stats.getType()){
            case Type.ACTIVE:
                act = stats.getValue();
                break;
            case Type.INACTIVE:
                inact = stats.getValue();
                break;
            case Type.MOVE_ACTIVE:
                moveAct = stats.getValue();
                break;
            case Type.MOVE_INACTIVE:
                moveInact = stats.getValue();
                break;
        }

        Long startOfToday = queryDate.getValue().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        Long endOfToday = queryDate.getValue().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        elapsedTime.postValue(now > endOfToday ? endOfToday - startOfToday : now -startOfToday);
        activityTime.postValue(act + moveAct + moveInact);
    };
}