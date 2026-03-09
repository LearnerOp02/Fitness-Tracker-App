package com.example.fitnessproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutHistoryActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView statsCard, filterCard;
    private TextView tvTotalWorkouts, tvTotalMinutes, tvTotalCalories, tvEmptyHistory;
    private Button btnFilterAll, btnFilterDaily, btnFilterWeekly;
    private ListView listWorkoutHistory;
    private FloatingActionButton fabAddWorkout;

    private JSONArray workoutHistory = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_history);

        initViews();
        setupToolbar();
        setupListeners();
        startAnimations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
        applyFilter("all");
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
//        statsCard = findViewById(R.id.statsCard);
//        filterCard = findViewById(R.id.filterCard);
        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvTotalMinutes = findViewById(R.id.tvTotalMinutes);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
//        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterDaily = findViewById(R.id.btnFilterDaily);
        btnFilterWeekly = findViewById(R.id.btnFilterWeekly);
        listWorkoutHistory = findViewById(R.id.listWorkoutHistory);
        fabAddWorkout = findViewById(R.id.fabAddWorkout);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Workout History", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(statsCard, 0);
        animateCard(filterCard, 150);
    }

    private void loadHistory() {
        String json = sessionManager.getWorkoutHistory();
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
                if (filter.equals("all")) include = true;
                else if (filter.equals("today")) include = date.equals(todayDate);
                else if (filter.equals("week")) include = date.compareTo(weekStart) >= 0;

                if (include) {
                    int dur = obj.optInt("duration", 0);
                    totalMinutes += dur;
                    String cal = obj.optString("calories", "0").replaceAll("[^0-9]", "");
                    if (!cal.isEmpty()) totalCalories += Integer.parseInt(cal);

                    String line = "🏋️ " + obj.optString("name", "Workout")
                            + " · " + obj.optString("type", "")
                            + "\n⏱ " + dur + " min  |  " + obj.optString("intensity", "")
                            + "  |  " + obj.optString("calories", "")
                            + "\n📅 " + obj.optString("displayDate", date);
                    displayList.add(line);
                }
            }
        } catch (Exception ignored) {}

        tvTotalWorkouts.setText(String.valueOf(displayList.size()));
        tvTotalMinutes.setText(String.valueOf(totalMinutes));
        tvTotalCalories.setText(String.valueOf(totalCalories));

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
                    tv.setBackgroundColor(getResources().getColor(R.color.surface));
                    tv.setPadding(24, 20, 24, 20);
                    tv.setTypeface(null, Typeface.NORMAL);
                    tv.setLineSpacing(4f, 1f);
                    return v;
                }
            };
            listWorkoutHistory.setAdapter(adapter);
        }

        resetFilterButtons();
        switch (filter) {
            case "all":
                styleActiveFilter(btnFilterAll);
                break;
            case "today":
                styleActiveFilter(btnFilterDaily);
                break;
            case "week":
                styleActiveFilter(btnFilterWeekly);
                break;
        }
    }

    private void resetFilterButtons() {
        for (Button b : new Button[]{btnFilterAll, btnFilterDaily, btnFilterWeekly}) {
            b.setTextColor(getResources().getColor(R.color.text_secondary));
            b.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
    }

    private void styleActiveFilter(Button b) {
        b.setTextColor(0xFFFFFFFF);
        b.setBackgroundResource(R.drawable.btn_primary_gradient);
    }

    private String getWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
    }

    private void setupListeners() {
        btnFilterAll.setOnClickListener(v -> {
            animateClick(v);
            applyFilter("all");
        });

        btnFilterDaily.setOnClickListener(v -> {
            animateClick(v);
            applyFilter("today");
        });

        btnFilterWeekly.setOnClickListener(v -> {
            animateClick(v);
            applyFilter("week");
        });

        fabAddWorkout.setOnClickListener(v -> {
            animateClick(v);
            navigateTo(AddWorkoutActivity.class);
        });
    }
}