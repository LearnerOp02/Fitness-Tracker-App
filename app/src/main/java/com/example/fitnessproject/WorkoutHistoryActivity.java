package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutHistoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView    tvTotalWorkouts, tvTotalMinutes, tvTotalCalories, tvEmptyHistory;
    private Button      btnFilterAll, btnFilterDaily, btnFilterWeekly;
    private ListView    listWorkoutHistory;
    private FloatingActionButton fabAddWorkout;

    private List<String> allEntries    = new ArrayList<>();
    private JSONArray    workoutHistory = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_history);
        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
        applyFilter("all");
    }

    private void initViews() {
        btnBack            = findViewById(R.id.btnBack);
        tvTotalWorkouts    = findViewById(R.id.tvTotalWorkouts);
        tvTotalMinutes     = findViewById(R.id.tvTotalMinutes);
        tvTotalCalories    = findViewById(R.id.tvTotalCalories);
        tvEmptyHistory     = findViewById(R.id.tvEmptyHistory);
        btnFilterAll       = findViewById(R.id.btnFilterAll);
        btnFilterDaily     = findViewById(R.id.btnFilterDaily);
        btnFilterWeekly    = findViewById(R.id.btnFilterWeekly);
        listWorkoutHistory = findViewById(R.id.listWorkoutHistory);
        fabAddWorkout      = findViewById(R.id.fabAddWorkout);
    }

    private void loadHistory() {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        String json = prefs.getString("workoutHistory", "[]");
        try {
            workoutHistory = new JSONArray(json);
        } catch (Exception e) {
            workoutHistory = new JSONArray();
        }
    }

    private void applyFilter(String filter) {
        List<String> displayList = new ArrayList<>();
        int totalMinutes = 0;
        int totalCalories = 0;

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String weekStart = getWeekStart();

        try {
            for (int i = workoutHistory.length() - 1; i >= 0; i--) {
                JSONObject obj = workoutHistory.getJSONObject(i);
                String date = obj.optString("date", "");

                boolean include = false;
                if      (filter.equals("all"))    include = true;
                else if (filter.equals("today"))  include = date.equals(todayDate);
                else if (filter.equals("week"))   include = date.compareTo(weekStart) >= 0;

                if (include) {
                    int dur = obj.optInt("duration", 0);
                    totalMinutes  += dur;
                    // Parse calorie number from "~210 kcal" string
                    String cal = obj.optString("calories", "0").replaceAll("[^0-9]", "");
                    if (!cal.isEmpty()) totalCalories += Integer.parseInt(cal);

                    String line = "🏋  " + obj.optString("name", "Workout")
                            + "  ·  " + obj.optString("type", "")
                            + "\n⏱ " + dur + " min  |  " + obj.optString("intensity", "")
                            + "  |  " + obj.optString("calories", "")
                            + "\n📅 " + obj.optString("displayDate", date);
                    displayList.add(line);
                }
            }
        } catch (Exception ignored) {}

        // Update summary strip
        tvTotalWorkouts.setText(String.valueOf(displayList.size()));
        tvTotalMinutes.setText(String.valueOf(totalMinutes));
        tvTotalCalories.setText(String.valueOf(totalCalories));

        // Show/hide empty state
        if (displayList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            listWorkoutHistory.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            listWorkoutHistory.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, displayList) {
                @Override
                public View getView(int pos, View convertView, android.view.ViewGroup parent) {
                    View v = super.getView(pos, convertView, parent);
                    TextView tv = v.findViewById(android.R.id.text1);
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setBackgroundColor(0xFF16213E);
                    tv.setPadding(24, 20, 24, 20);
                    tv.setTypeface(null, Typeface.NORMAL);
                    tv.setLineSpacing(4f, 1f);
                    return v;
                }
            };
            listWorkoutHistory.setAdapter(adapter);
        }

        // Highlight active filter button
        resetFilterButtons();
        switch (filter) {
            case "all":   styleActiveFilter(btnFilterAll);   break;
            case "today": styleActiveFilter(btnFilterDaily); break;
            case "week":  styleActiveFilter(btnFilterWeekly);break;
        }
    }

    private void resetFilterButtons() {
        for (Button b : new Button[]{btnFilterAll, btnFilterDaily, btnFilterWeekly}) {
            b.setTextColor(0xFFAAAAAA);
            b.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
    }

    private void styleActiveFilter(Button b) {
        b.setTextColor(0xFFFFFFFF);
        b.setBackgroundResource(R.drawable.btn_primary_bg);
    }

    private String getWeekStart() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnFilterAll.setOnClickListener(v -> applyFilter("all"));
        btnFilterDaily.setOnClickListener(v -> applyFilter("today"));
        btnFilterWeekly.setOnClickListener(v -> applyFilter("week"));
        fabAddWorkout.setOnClickListener(v ->
                startActivity(new Intent(this, AddWorkoutActivity.class)));
    }
}