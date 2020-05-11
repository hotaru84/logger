package com.example.demo.logsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.ui.StatsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    StatsViewModel statsViewModel;
    Chip datePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statsViewModel = new StatsViewModel();
        initObservables();
        findViewById(R.id.enable_sw).setOnClickListener((c)-> toggleService());
        datePicker = findViewById(R.id.date_pick);
        datePicker.setOnClickListener((c)-> showDateDialog());
        statsViewModel.getObservableTargetDate().observe(this,(date)->{
            if(date == null) return;
            String s = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            datePicker.setText(s);
        });
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_usage, R.id.navigation_status, R.id.navigation_log)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        statsViewModel.setTargetDate(LocalDate.now());
    }

    private void initObservables() {
        LogRepository repository = LogRepository.getInstance();
        repository.init(getApplication());
    }
    private void showDateDialog(){
        LocalDate now = LocalDate.parse(datePicker.getText());
        DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, y, m, d) -> {
            statsViewModel.setTargetDate(LocalDate.of(y,m,d));
        },now.getYear(),now.getMonthValue(),now.getDayOfMonth());
        dialog.show();
    }
    private void toggleService() {
        Intent serviceIntent = new Intent(this, LoggerService.class);
        if(LoggerService.isRunning){
            stopService(serviceIntent);
        } else{
            serviceIntent.setAction(LoggerService.ACTION_START_SERVICE);
            startForegroundService(serviceIntent);
        }
    }
}
