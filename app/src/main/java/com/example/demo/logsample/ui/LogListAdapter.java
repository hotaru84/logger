package com.example.demo.logsample.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.logsample.R;
import com.example.demo.logsample.databinding.LogItemBinding;
import com.example.demo.logsample.log.Log;

import java.util.ArrayList;
import java.util.List;

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.LogItem> {
    private List<Log> logList = new ArrayList<>();
    private LifecycleOwner lifecycleOwner;
    public LogListAdapter(LifecycleOwner owner) {
        lifecycleOwner = owner;
    }
    public void setList(List<Log> lists) {
        logList = lists;
    }
    @NonNull
    @Override
    public LogItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.log_item,
                parent,
                false);
        return new LogItem(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LogItem holder, int position) {
        holder.getBinding().setLifecycleOwner(lifecycleOwner);
        holder.getBinding().setLog(logList.get(position));
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }
    class LogItem extends RecyclerView.ViewHolder{
        private LogItemBinding logItemBinding;
        public LogItem(LogItemBinding binding) {
            super(binding.getRoot());
            logItemBinding = binding;
        }
        public LogItemBinding getBinding(){return logItemBinding;}
    }
}
