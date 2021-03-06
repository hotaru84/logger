package com.example.demo.logsample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.logsample.R;
import com.example.demo.logsample.databinding.FragmentUsageBinding;


public class UsageFragment extends Fragment {
    LogViewModel logViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        logViewModel = new ViewModelProvider(requireActivity()).get(LogViewModel.class);
        FragmentUsageBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_usage,container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(logViewModel);
        return binding.getRoot();
    }
}
