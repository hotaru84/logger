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
import com.example.demo.logsample.databinding.FragmentLoglistBinding;

public class LogListFragment extends Fragment {
    private LogViewModel logViewModel;
    private LogListAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        logViewModel = new ViewModelProvider(requireActivity()).get(LogViewModel.class);
        adapter = new LogListAdapter(getViewLifecycleOwner());

        FragmentLoglistBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_loglist,container,false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.recyclerView.setAdapter(adapter);
        logViewModel.queryLogList().observe(getViewLifecycleOwner(),list->{
            adapter.setList(list);
            binding.recyclerView.setAdapter(adapter);
        });
        return binding.getRoot();
    }
}
