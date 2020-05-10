package com.example.demo.logsample.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Type;

import java.time.LocalDate;

public class StatsViewModel extends ViewModel {
    private LiveData<Long> observableActiveTime;
    private LiveData<Long> observableInactiveTime;
    private LiveData<Long> observableMoveActiveTime;
    private LiveData<Long> observableMoveInactiveTime;
    public StatsViewModel() {
        LogRepository repo = LogRepository.getInstance();
        observableActiveTime = repo.observeStats(LocalDate.now(), Type.ACTIVE);
        observableInactiveTime = repo.observeStats(LocalDate.now(), Type.INACTIVE);
        observableMoveActiveTime = repo.observeStats(LocalDate.now(), Type.MOVE_ACTIVE);
        observableMoveInactiveTime = repo.observeStats(LocalDate.now(), Type.MOVE_INACTIVE);
    }
    public Long getActiveTime() {
        return LogRepository.getInstance().getStatsValue(LocalDate.now(),Type.ACTIVE);
    }
    public Long getInactiveTime() {
        return LogRepository.getInstance().getStatsValue(LocalDate.now(),Type.INACTIVE);
    }
    public Long getMoveActiveTime() {
        return LogRepository.getInstance().getStatsValue(LocalDate.now(),Type.MOVE_ACTIVE);
    }
    public Long getMoveInactiveTime() {
        return LogRepository.getInstance().getStatsValue(LocalDate.now(),Type.MOVE_INACTIVE);
    }
    public LiveData<Long> getObservableActiveTime() {
        return observableActiveTime;
    }

    public LiveData<Long> getObservableInactiveTime() {
        return observableInactiveTime;
    }

    public LiveData<Long> getObservableMoveActiveTime() {
        return observableMoveActiveTime;
    }

    public LiveData<Long> getObservableMoveInactiveTime() {
        return observableMoveInactiveTime;
    }
}