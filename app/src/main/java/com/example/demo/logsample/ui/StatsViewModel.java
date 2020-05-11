package com.example.demo.logsample.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Type;

import java.time.LocalDate;

public class StatsViewModel extends ViewModel {
    private LogRepository repo;
    public StatsViewModel() {
        repo = LogRepository.getInstance();
    }
    public void setTargetDate(LocalDate date) {
        LogRepository.getInstance().setQueryDate(date);
    }
    public Long getActiveTime() {
        return LogRepository.getInstance().getStatsValue(Type.ACTIVE);
    }
    public Long getInactiveTime() {
        return LogRepository.getInstance().getStatsValue(Type.INACTIVE);
    }
    public Long getMoveActiveTime() {
        return LogRepository.getInstance().getStatsValue(Type.MOVE_ACTIVE);
    }
    public Long getMoveInactiveTime() {
        return LogRepository.getInstance().getStatsValue(Type.MOVE_INACTIVE);
    }
    public LiveData<Long> getObservableActiveTime() {
        return repo.observeStats(Type.ACTIVE);
    }
    public LiveData<LocalDate> getObservableTargetDate() {
        return repo.observeDate();
    }
    public LiveData<Long> getObservableInactiveTime() {
        return repo.observeStats( Type.INACTIVE);
    }

    public LiveData<Long> getObservableMoveActiveTime() {
        return repo.observeStats( Type.MOVE_ACTIVE);
    }

    public LiveData<Long> getObservableMoveInactiveTime() {
        return repo.observeStats( Type.MOVE_INACTIVE);
    }
}