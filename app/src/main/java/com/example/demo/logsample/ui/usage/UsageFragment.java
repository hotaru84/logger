package com.example.demo.logsample.ui.usage;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.demo.logsample.R;
import com.example.demo.logsample.log.LogRepository;
import com.example.demo.logsample.log.Type;
import com.example.demo.logsample.ui.StatsViewModel;

import java.time.LocalDate;

public class UsageFragment extends Fragment {
    StatsViewModel statsViewModel;
    View activeStats;
    View moveStats;
    View ttlStats;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statsViewModel = new StatsViewModel();
        View root = inflater.inflate(R.layout.fragment_usage, container, false);
        activeStats = root.findViewById(R.id.activeTime);
        moveStats = root.findViewById(R.id.movingTime);
        ttlStats = root.findViewById(R.id.ttlTime);
        TextView alabel = activeStats.findViewById(R.id.label_text);
        ImageView aIcon = activeStats.findViewById(R.id.icon);
        TextView mlabel = moveStats.findViewById(R.id.label_text);
        ImageView mIcon = moveStats.findViewById(R.id.icon);

        alabel.setText("Active");
        aIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_app_black_24dp,null));
        mlabel.setText("Moving");
        mIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_black_24dp,null));
        statsViewModel.getObservableActiveTime().observe(getViewLifecycleOwner(),s->update());
        statsViewModel.getObservableInactiveTime().observe(getViewLifecycleOwner(),s->update());
        statsViewModel.getObservableMoveActiveTime().observe(getViewLifecycleOwner(),s->update());
        statsViewModel.getObservableMoveInactiveTime().observe(getViewLifecycleOwner(),s->update());

        return root;
    }
    private void update() {
        TextView activeValue = activeStats.findViewById(R.id.value_text);
        ProgressBar activeProgress = activeStats.findViewById(R.id.progress);
        TextView moveValue = moveStats.findViewById(R.id.value_text);
        ProgressBar moveProgress = moveStats.findViewById(R.id.progress);
        TextView ttlValue = ttlStats.findViewById(R.id.value_text);
        int activettl = (int) (statsViewModel.getActiveTime() + statsViewModel.getMoveActiveTime());
        int inactivettl = (int) (statsViewModel.getInactiveTime() + statsViewModel.getMoveInactiveTime());
        int movettl = (int) (statsViewModel.getMoveActiveTime() + statsViewModel.getMoveInactiveTime());
        int ttl = activettl + inactivettl;

        activeValue.setText(String.format("%.2f",((float)activettl)/60/60));
        activeProgress.setMax(ttl);
        activeProgress.setProgress(activettl,true);

        moveValue.setText(String.format("%.2f",((float)movettl)/60/60));
        moveProgress.setMax(ttl);
        moveProgress.setProgress(movettl,true);

        ttlValue.setText(String.format("%.2f",((float)ttl)/60/60));
    }
}
