package com.example.demo.logsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    public static int SHOW_REQUEST = 1;

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
    }
}
