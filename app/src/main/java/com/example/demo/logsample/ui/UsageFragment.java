package com.example.demo.logsample.ui;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.demo.logsample.R;
import com.example.demo.logsample.databinding.FragmentUsageBinding;

import java.time.format.DateTimeFormatter;


public class UsageFragment extends Fragment {
    StatsViewModel statsViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statsViewModel = new ViewModelProvider(requireActivity()).get(StatsViewModel.class);
        FragmentUsageBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_usage,container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(statsViewModel);
        return binding.getRoot();
    }
}
