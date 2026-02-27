package com.example.fitnessproject;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MonthlyProgressActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvMonthLabel, tvMonthWorkouts, tvMonthDuration,
            tvMonthConsistency, tvAchievementTitle, tvAchievementDesc;
    private BarChartView barChart;
    private LineChartView lineChart;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_progress);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
        initViews();
        loadMonthlyData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvMonthLabel = findViewById(R.id.tvMonthLabel);
        tvMonthWorkouts = findViewById(R.id.tvMonthWorkouts);
        tvMonthDuration = findViewById(R.id.tvMonthDuration);
        tvMonthConsistency = findViewById(R.id.tvMonthConsistency);
        tvAchievementTitle = findViewById(R.id.tvAchievementTitle);
        tvAchievementDesc = findViewById(R.id.tvAchievementDesc);
        barChart = findViewById(R.id.barChart);
        lineChart = findViewById(R.id.lineChart);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadMonthlyData() {
        String month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        tvMonthLabel.setText(month);

        String json = sessionManager.getWorkoutHistory();

        int totalWorkouts = 0, totalDuration = 0;
        int[] weekDurations = new int[4];
        int[] dayFlags = new int[31];

        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        Calendar cal = Calendar.getInstance();

        try {
            JSONArray history = new JSONArray(json);
            for (int i = 0; i < history.length(); i++) {
                JSONObject obj = history.getJSONObject(i);
                String date = obj.optString("date", "");
                if (!date.startsWith(currentMonth)) continue;

                int dur = obj.optInt("duration", 0);
                totalWorkouts++;
                totalDuration += dur;

                try {
                    cal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date));
                    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                    int weekIndex = Math.min((dayOfMonth - 1) / 7, 3);
                    weekDurations[weekIndex] += dur;
                    if (dayOfMonth <= 31) dayFlags[dayOfMonth - 1] = 1;
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        int daysElapsed = cal.get(Calendar.DAY_OF_MONTH);
        int activeDays = 0;
        for (int i = 0; i < daysElapsed; i++) if (dayFlags[i] == 1) activeDays++;
        int consistency = daysElapsed > 0 ? (activeDays * 100 / daysElapsed) : 0;

        tvMonthWorkouts.setText(String.valueOf(totalWorkouts));
        tvMonthDuration.setText(String.valueOf(totalDuration));
        tvMonthConsistency.setText(consistency + "%");

        barChart.setData(new float[]{
                weekDurations[0], weekDurations[1], weekDurations[2], weekDurations[3]
        }, new String[]{"W1", "W2", "W3", "W4"});

        float[] lineData = new float[Math.min(daysElapsed, 31)];
        int cum = 0;
        for (int i = 0; i < lineData.length; i++) {
            cum += dayFlags[i];
            lineData[i] = cum;
        }
        lineChart.setData(lineData);

        if (consistency >= 80) {
            tvAchievementTitle.setText("🥇 Consistency Champion!");
            tvAchievementDesc.setText("You worked out " + activeDays + " days this month. Incredible!");
        } else if (consistency >= 50) {
            tvAchievementTitle.setText("🥈 Great Progress!");
            tvAchievementDesc.setText("More than half the month active. Keep it up!");
        } else {
            tvAchievementTitle.setText("💪 Keep Going!");
            tvAchievementDesc.setText("You have " + (daysElapsed - activeDays) + " missed days. Push harder next week!");
        }
    }
}