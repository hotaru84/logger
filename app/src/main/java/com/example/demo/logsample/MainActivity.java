package com.example.demo.logsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    public static int SHOW_REQUEST = 1;
    private LogRepository logRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Switch enableSw = findViewById(R.id.track_enable_sw);
        logRepository = new LogRepository(getApplication());
        logRepository.getAllLogs().observe(this, logs -> {
            TextView v = findViewById(R.id.log_text);
            v.setText("");
            logs.forEach(log -> v.append(
                    new Gson().toJson(log.getJsonObject()) + "\n")
            );
        });
        logRepository.getAllStats().observe(this, stats ->{
            TextView v = findViewById(R.id.log_text);
            v.setText("");
            stats.forEach(s -> v.append(
                    new Gson().toJson(s.getJsonObject()) + "\n")
            );
        });
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
            logRepository.deleteAll();

        });

        findViewById(R.id.add).setOnClickListener(v->{
            String localDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YY-MM-DD HH:mm:ss.SSS"));
            logRepository.insert(new Log(System.currentTimeMillis(),localDate));
        });
    }
}
