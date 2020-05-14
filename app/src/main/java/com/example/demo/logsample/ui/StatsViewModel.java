package com.example.demo.logsample.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.demo.logsample.log.Log;
import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Stats;
import com.example.demo.logsample.log.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatsViewModel extends AndroidViewModel {
    private MutableLiveData<LocalDate> queryDate = new MutableLiveData<>();
    private LiveData<Stats> active;
    private LiveData<Stats> inactive;
    private LiveData<Stats> moveActive;
    private LiveData<Stats> moveInactive;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        LogRepository.getInstance().init(application);
        active = queryStats(Type.ACTIVE);
        inactive = queryStats(Type.INACTIVE);
        moveActive = queryStats(Type.MOVE_ACTIVE);
        moveInactive = queryStats(Type.MOVE_INACTIVE);
    }

    public void setTargetDate(LocalDate date) {
        queryDate.setValue(date);
    }
    public LiveData<String> getTargetDate() {
        return Transformations.map(queryDate, d->d.format(DateTimeFormatter.ISO_DATE));
    }
    private LiveData<Stats> queryStats(String type) {
        return Transformations.switchMap(queryDate,q-> LogRepository.getInstance().getStats(q, type));
    }

    public LiveData<Stats> getActive() {
        return active;
    }

    public LiveData<Stats> getInactive() {
        return inactive;
    }

    public LiveData<Stats> getMoveActive() {
        return moveActive;
    }

    public LiveData<Stats> getMoveInactive() {
        return moveInactive;
    }
}