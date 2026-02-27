package com.example.fitnessproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class WorkoutPlanActivity extends AppCompatActivity {

    private ImageButton  btnBack;
    private TextView     tvPlanTitle, tvProfileSummary, tvAdaptiveBadge,
            tvAdaptiveReason;
    private CardView     cardAdaptiveReason;
    private LinearLayout layoutPlanDays;

    // Pass "adaptive" = true from intent to show Adaptive screen
    private boolean isAdaptive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_plan);

        isAdaptive = getIntent().getBooleanExtra("adaptive", false);

        initViews();
        buildPlan();
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        btnBack            = findViewById(R.id.btnBack);
        tvPlanTitle        = findViewById(R.id.tvPlanTitle);
        tvProfileSummary   = findViewById(R.id.tvProfileSummary);
        tvAdaptiveBadge    = findViewById(R.id.tvAdaptiveBadge);
        cardAdaptiveReason = findViewById(R.id.cardAdaptiveReason);
        tvAdaptiveReason   = findViewById(R.id.tvAdaptiveReason);
        layoutPlanDays     = findViewById(R.id.layoutPlanDays);
    }

    private void buildPlan() {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);

        float  bmi      = prefs.getFloat("userBMI", 22f);
        String goal     = prefs.getString("userGoal", "Build Muscle");
        String level    = prefs.getString("userActivityLevel", "Moderately Active");
        int    streak   = prefs.getInt("workoutStreak", 0);
        int    score    = prefs.getInt("disciplineScore", 87);

        // Profile summary
        tvProfileSummary.setText(String.format("BMI: %.1f  |  Goal: %s  |  Level: %s", bmi, goal, level));

        if (isAdaptive) {
            tvPlanTitle.setText("Adaptive Workout Plan");
            tvAdaptiveBadge.setVisibility(View.VISIBLE);
            cardAdaptiveReason.setVisibility(View.VISIBLE);

            // Reason logic
            String reason;
            if (streak == 0)         reason = "Plan lightened — no workouts logged recently. Start slow!";
            else if (score < 50)     reason = "Intensity reduced — consistency score below 50%. Build the habit first.";
            else if (streak >= 7)    reason = "Plan intensified — 7+ day streak detected! You're on fire 🔥";
            else                     reason = "Plan adapted to your current activity level and progress.";
            tvAdaptiveReason.setText(reason);
        } else {
            tvPlanTitle.setText("Your Workout Plan");
        }

        // Generate plan based on BMI + goal
        String[][] plan = generatePlan(bmi, goal, level, isAdaptive, score);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            addDayCard(days[i], plan[i][0], plan[i][1], plan[i][2]);
        }
    }

    // Returns [exerciseName, duration, sets/reps] per day
    private String[][] generatePlan(float bmi, String goal, String level,
                                    boolean adaptive, int score) {
        // Intensity modifier
        float factor = adaptive ? (score < 50 ? 0.7f : (score >= 80 ? 1.2f : 1.0f)) : 1.0f;

        boolean isOverweight = bmi >= 25;
        boolean isMuscle     = goal.toLowerCase().contains("muscle") || goal.toLowerCase().contains("strength");
        boolean isCardio     = goal.toLowerCase().contains("weight") || goal.toLowerCase().contains("slim");
        boolean isBeginner   = level.toLowerCase().contains("sedentary") || level.toLowerCase().contains("light");

        int dur = (int)(30 * factor);
        int durHard = (int)(45 * factor);
        String rest = "Rest & Stretching — 15 min";

        if (isCardio || isOverweight) {
            return new String[][]{
                    {"Brisk Walk / Jog", dur + " min", "Moderate pace"},
                    {"Cycling or Elliptical", durHard + " min", "Interval: 1 min fast / 2 min slow"},
                    {"Bodyweight HIIT", (int)(20 * factor) + " min", "3 sets × 10 reps"},
                    {"Swimming or Treadmill", dur + " min", "Steady state cardio"},
                    {rest, "15 min", "Yoga / foam roll"},
                    {"Outdoor Run", durHard + " min", "Target 5 km"},
                    {"Active Rest", "20 min", "Light walk or stretching"}
            };
        } else if (isMuscle) {
            return new String[][]{
                    {"Chest & Triceps", durHard + " min", "Bench Press 4×10, Push-ups 3×15"},
                    {"Back & Biceps", durHard + " min", "Pull-ups 3×8, Dumbbell Rows 4×10"},
                    {rest, "15 min", "Foam roll + stretching"},
                    {"Legs & Glutes", durHard + " min", "Squats 4×12, Lunges 3×10"},
                    {"Shoulders & Core", dur + " min", "OHP 3×10, Plank 3×60s"},
                    {"Full Body Circuit", durHard + " min", "5 exercises × 3 sets"},
                    {"Rest", "—", "Sleep & recovery is growth"}
            };
        } else {
            // General fitness
            return new String[][]{
                    {"Morning Walk + Core", dur + " min", "3 sets × 15 reps"},
                    {"Yoga / Flexibility", dur + " min", "Sun salutation + stretches"},
                    {"Light Cardio", dur + " min", "20 min walk, 10 min jog"},
                    {rest, "15 min", "Recovery"},
                    {"Bodyweight Strength", dur + " min", "Squats, push-ups, planks"},
                    {"Outdoor Activity", durHard + " min", "Cycling, badminton, or swimming"},
                    {"Rest", "—", "Full rest"}
            };
        }
    }

    private void addDayCard(String day, String exercise, String duration, String detail) {
        // Outer card
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(0xFF16213E);
        card.setRadius(40f);
        card.setCardElevation(4f);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(40, 36, 40, 36);
        row.setGravity(Gravity.CENTER_VERTICAL);

        // Day label
        TextView tvDay = new TextView(this);
        tvDay.setText(day.substring(0, 3).toUpperCase());
        tvDay.setTextColor(0xFFE94560);
        tvDay.setTextSize(12f);
        tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
        tvDay.setMinWidth(120);

        // Details column
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        col.setLayoutParams(colP);

        TextView tvExercise = new TextView(this);
        tvExercise.setText(exercise);
        tvExercise.setTextColor(Color.WHITE);
        tvExercise.setTextSize(14f);
        tvExercise.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDetail = new TextView(this);
        tvDetail.setText(duration + "  ·  " + detail);
        tvDetail.setTextColor(0xFFAAAAAA);
        tvDetail.setTextSize(11f);
        tvDetail.setLineSpacing(4f, 1f);

        col.addView(tvExercise);
        col.addView(tvDetail);

        row.addView(tvDay);
        row.addView(col);
        card.addView(row);
        layoutPlanDays.addView(card);
    }
}