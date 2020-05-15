package com.example.demo.logsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.demo.logsample.databinding.ActivityMainBinding;
import com.example.demo.logsample.ui.LogViewModel;
import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    LogViewModel logViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);
        logViewModel.setQueryDate(LocalDate.now());
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.setVm(logViewModel);
        binding.enableSw.setOnClickListener((c)-> toggleService());
        binding.datePick.setOnClickListener((c)-> {
            Chip v = (Chip) c;
            LocalDate date = LocalDate.parse(v.getText(),DateTimeFormatter.ISO_DATE);
            DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, y, m, d) -> {
                logViewModel.setQueryDate(LocalDate.of(y,m+1,d));
            },date.getYear(),date.getMonthValue()-1,date.getDayOfMonth());
            dialog.show();
        });
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);
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
