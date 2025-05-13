package com.example.todoappv2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.media.Ringtone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.adapter.AppUsageAdapter;
import com.example.todoappv2.model.AppUsage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

public class FocusActivity extends AppCompatActivity {
    private TextView timerTextView;
    private EditText inputMinutes;
    private Button startButton;
    private CountDownTimer countDownTimer;
    private long focusTimeMillis = 0;
    private boolean isFocusing = false;
    private RecyclerView usageRecyclerView;
    private AppUsageAdapter usageAdapter;
    private MediaPlayer mediaPlayer;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        timerTextView = findViewById(R.id.timerTextView);
        inputMinutes = findViewById(R.id.inputMinutes);
        startButton = findViewById(R.id.startButton);
        usageRecyclerView = findViewById(R.id.usageRecyclerView);
        BarChart barChart = findViewById(R.id.barChart);

        // Initialize pulse animation
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);

        usageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<AppUsage> usageList;
        if (!hasUsageStatsPermission()) {
            Toast.makeText(this, "Please grant usage access permission", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            usageList = new ArrayList<>();
        } else {
            usageList = getRealAppUsage();
        }
        usageAdapter = new AppUsageAdapter(usageList);
        usageRecyclerView.setAdapter(usageAdapter);
        showUsageBarChart(barChart, usageList);

        inputMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isFocusing) {
                    updateTimerDisplay();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        startButton.setOnClickListener(v -> {
            if (!isFocusing) {
                String minStr = inputMinutes.getText().toString();
                if (minStr.isEmpty()) {
                    Toast.makeText(this, "Please enter minutes", Toast.LENGTH_SHORT).show();
                    return;
                }
                int minutes = Integer.parseInt(minStr);
                focusTimeMillis = minutes * 60 * 1000L;
                startFocusTimer();
            } else {
                stopFocusTimer();
            }
        });

        updateTimerDisplay();
    }

    private void startFocusTimer() {
        isFocusing = true;
        inputMinutes.setEnabled(false);
        startButton.setText("Stop Focusing");
        countDownTimer = new CountDownTimer(focusTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                focusTimeMillis = millisUntilFinished;
                updateTimerDisplay();
            }
            @Override
            public void onFinish() {
                isFocusing = false;
                inputMinutes.setEnabled(true);
                startButton.setText("Start Focusing");

                // Play ringtone
                playCompletionRingtone();

                // Start pulse animation
                timerTextView.startAnimation(pulseAnimation);

                Toast.makeText(FocusActivity.this, "Focus session complete!", Toast.LENGTH_SHORT).show();
                updateTimerDisplay();
            }
        }.start();
    }

    private void stopFocusTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isFocusing = false;
        inputMinutes.setEnabled(true);
        startButton.setText("Start Focusing");
        timerTextView.clearAnimation();
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        long totalSeconds = focusTimeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private List<AppUsage> getRealAppUsage() {
        List<AppUsage> list = new ArrayList<>();
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        Map<String, AppInfo> appInfoMap = new HashMap<>();
        appInfoMap.put("com.instagram.android", new AppInfo("Instagram", android.R.drawable.ic_menu_camera));
        appInfoMap.put("com.twitter.android", new AppInfo("X", android.R.drawable.ic_menu_info_details));
        appInfoMap.put("com.facebook.katana", new AppInfo("Facebook", android.R.drawable.ic_menu_gallery));
        appInfoMap.put("org.telegram.messenger", new AppInfo("Telegram", android.R.drawable.ic_menu_send));
        appInfoMap.put("com.google.android.gm", new AppInfo("Gmail", android.R.drawable.ic_dialog_email));
        appInfoMap.put("com.google.android.youtube", new AppInfo("YouTube", android.R.drawable.ic_media_play));
        // Aggregate usage time per app
        Map<String, Long> usageTimeMap = new HashMap<>();
        for (UsageStats usageStats : stats) {
            String packageName = usageStats.getPackageName();
            if (appInfoMap.containsKey(packageName)) {
                long totalTime = usageStats.getTotalTimeInForeground();
                usageTimeMap.put(packageName, usageTimeMap.getOrDefault(packageName, 0L) + totalTime);
            }
        }
        for (Map.Entry<String, Long> entry : usageTimeMap.entrySet()) {
            AppInfo info = appInfoMap.get(entry.getKey());
            String timeStr = formatMillis(entry.getValue());
            list.add(new AppUsage(info.name, timeStr, info.iconResId));
        }
        if (list.isEmpty()) {
            list.add(new AppUsage("No data", "-", android.R.drawable.ic_menu_help));
        }
        return list;
    }

    private String formatMillis(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);
    }

    private void showUsageBarChart(BarChart barChart, List<AppUsage> usageList) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int i = 0;
        for (AppUsage usage : usageList) {
            // Parse hours and minutes from usage.getUsageTime()
            String time = usage.getUsageTime();
            int hours = 0, minutes = 0;
            if (time.contains("h")) {
                String[] parts = time.split("h");
                try { hours = Integer.parseInt(parts[0].trim()); } catch (Exception ignored) {}
                if (parts.length > 1 && parts[1].contains("m")) {
                    String minStr = parts[1].replace("m", "").trim();
                    if (!minStr.isEmpty()) try { minutes = Integer.parseInt(minStr); } catch (Exception ignored) {}
                }
            } else if (time.contains("m")) {
                String minStr = time.replace("m", "").trim();
                if (!minStr.isEmpty()) try { minutes = Integer.parseInt(minStr); } catch (Exception ignored) {}
            }
            float totalMinutes = hours * 60 + minutes;
            entries.add(new BarEntry(i, totalMinutes));
            labels.add(usage.getAppName());
            i++;
        }
        BarDataSet dataSet = new BarDataSet(entries, "Usage (minutes)");
        BarData data = new BarData(dataSet);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
    }

    private void playCompletionRingtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (notification == null) {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            if (ringtone != null) {
                ringtone.play();
            } else {
                Toast.makeText(this, "No ringtone found!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to play ringtone!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private static class AppInfo {
        String name;
        int iconResId;
        AppInfo(String name, int iconResId) {
            this.name = name;
            this.iconResId = iconResId;
        }
    }
} 