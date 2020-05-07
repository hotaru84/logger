package com.example.demo.logsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Type;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String,Long> usageSeconds = new HashMap<>();
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

        repository.observeStats(LocalDate.now(), Type.ACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.activeSeconds);
            textView.setText("Active:" + seconds + "sec");
            usageSeconds.put(Type.ACTIVE,seconds);
        });
        repository.observeStats(LocalDate.now(), Type.MOVE_ACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.moveActiveSeconds);
            textView.setText("Move Active:" + seconds + "sec");
            usageSeconds.put(Type.MOVE_ACTIVE,seconds);
        });
        repository.observeStats(LocalDate.now(), Type.MOVE_INACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.moveInactiveSeconds);
            textView.setText("Move Inactive:" + seconds + "sec");
            usageSeconds.put(Type.MOVE_INACTIVE,seconds);
        });
        repository.observeStats(LocalDate.now(), Type.INACTIVE).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.inactiveSeconds);
            textView.setText("Inactive:" + seconds + "sec");
            usageSeconds.put(Type.INACTIVE,seconds);
        });
        repository.observeStats(LocalDate.now(), Type.WIFI_GOOD).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.wifiGood);
            textView.setText("wifiGood:" + seconds + "sec");
            usageSeconds.put(Type.WIFI_GOOD,seconds);
        });

        repository.observeStats(LocalDate.now(), Type.WIFI_MID).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.wifiMid);
            textView.setText("wifiMid:" + seconds + "sec");
            usageSeconds.put(Type.WIFI_MID,seconds);
        });

        repository.observeStats(LocalDate.now(), Type.WIFI_LOW).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.wifiLow);
            textView.setText("wifiLow:" + seconds + "sec");
            usageSeconds.put(Type.WIFI_LOW,seconds);
        });

        repository.observeStats(LocalDate.now(), Type.WIFI_LOST).observe(this, seconds -> {
            if(seconds == null) return;
            TextView textView = findViewById(R.id.wifiLost);
            textView.setText("wifiLost:" + seconds + "sec");
            usageSeconds.put(Type.WIFI_LOST,seconds);
        });

    }
}
