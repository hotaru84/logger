package com.example.demo.logsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.Switch;
import android.widget.TextView;

import com.example.demo.logsample.log.Log;
import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Type;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MainActivity extends AppCompatActivity {
    private Map<String,Long> usageSeconds = new HashMap<>();
    private MutableLiveData<Long> ttlSeconds = new MutableLiveData<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Switch enableSw = findViewById(R.id.track_enable_sw);

        enableSw.setChecked(LoggerService.isRunning);
        enableSw.setOnCheckedChangeListener((compoundButton, b) -> {
            Intent serviceIntent = new Intent(this, LoggerService.class);
            if(!b && LoggerService.isRunning){
                stopService(serviceIntent);
            } else if(b && !LoggerService.isRunning) {
                serviceIntent.setAction(LoggerService.ACTION_START_SERVICE);
                startForegroundService(serviceIntent);
            }
        });
        findViewById(R.id.clear).setOnClickListener(v->{
            LogRepository.getInstance().deleteAll();
        });
        initObservables();
    }
    private void initObservables() {
        LogRepository repository = LogRepository.getInstance();
        repository.init(getApplication());

        ttlSeconds.observe(this,v -> {
            Long ttl = usageSeconds.values().stream().mapToLong(Long::longValue).sum();
            TextView textView = findViewById(R.id.ttlSeconds);
            textView.setText("Total:" + ttl + "sec");
        });

        repository.observeStats(LocalDate.now(), Type.ACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.activeSeconds);
            textView.setText("Active:" + seconds + "sec");
            usageSeconds.put(Type.ACTIVE,seconds);
            ttlSeconds.postValue(0L);
        });
        repository.observeStats(LocalDate.now(), Type.MOVE_ACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.moveActiveSeconds);
            textView.setText("Move Active:" + seconds + "sec");
            usageSeconds.put(Type.MOVE_ACTIVE,seconds);
            ttlSeconds.postValue(0L);
        });
        repository.observeStats(LocalDate.now(), Type.MOVE_INACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.moveInactiveSeconds);
            textView.setText("Move Inactive:" + seconds + "sec");
            usageSeconds.put(Type.MOVE_INACTIVE,seconds);
            ttlSeconds.postValue(0L);
        });
        repository.observeStats(LocalDate.now(), Type.INACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.inactiveSeconds);
            textView.setText("Inactive:" + seconds + "sec");
            usageSeconds.put(Type.INACTIVE,seconds);
            ttlSeconds.postValue(0L);
        });
    }
}
