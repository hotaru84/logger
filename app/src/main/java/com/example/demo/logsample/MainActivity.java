package com.example.demo.logsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

import com.example.demo.logsample.log.LogRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObservables();
        Switch sw = findViewById(R.id.enable_sw);
        sw.setChecked(LoggerService.isRunning);
        sw.setOnCheckedChangeListener((c,b)-> toggleService(b));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_usage, R.id.navigation_status, R.id.navigation_log)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void initObservables() {
        LogRepository repository = LogRepository.getInstance();
        repository.init(getApplication());
    }
    private void toggleService(boolean b) {
        Intent serviceIntent = new Intent(this, LoggerService.class);
        if(!b && LoggerService.isRunning){
            stopService(serviceIntent);
        } else if(b && !LoggerService.isRunning){
            serviceIntent.setAction(LoggerService.ACTION_START_SERVICE);
            startForegroundService(serviceIntent);
        }
    }
}
