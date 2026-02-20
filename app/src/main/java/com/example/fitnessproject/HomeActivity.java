package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private TextView   tvGreeting, tvUserName;
    private TextView   tvBMI, tvBMIStatus;
    private TextView   tvDisciplineScore, tvStreak;
    private TextView   tvWorkoutTitle, tvWorkoutProgress;
    private ProgressBar progressWorkout;
    private Button     btnStartWorkout;
    private CardView   cardAddWorkout, cardViewProgress, cardMyProfile, cardNutrition;

    // Track back-press for exit confirmation
    private boolean backPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initViews();
        loadDashboard();
        setupQuickActions();
    }

    // Refresh dashboard every time user returns from another screen
    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void initViews() {
        tvGreeting        = findViewById(R.id.tvGreeting);
        tvUserName        = findViewById(R.id.tvUserName);
        tvBMI             = findViewById(R.id.tvBMI);
        tvBMIStatus       = findViewById(R.id.tvBMIStatus);
        tvDisciplineScore = findViewById(R.id.tvDisciplineScore);
        tvStreak          = findViewById(R.id.tvStreak);
        tvWorkoutTitle    = findViewById(R.id.tvWorkoutTitle);
        tvWorkoutProgress = findViewById(R.id.tvWorkoutProgress);
        progressWorkout   = findViewById(R.id.progressWorkout);
        btnStartWorkout   = findViewById(R.id.btnStartWorkout);
        cardAddWorkout    = findViewById(R.id.cardAddWorkout);
        cardViewProgress  = findViewById(R.id.cardViewProgress);
        cardMyProfile     = findViewById(R.id.cardMyProfile);
        cardNutrition     = findViewById(R.id.cardNutrition);
    }

    private void loadDashboard() {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);

        // ── Time-based greeting ──
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if      (hour < 12) greeting = "Good Morning,";
        else if (hour < 17) greeting = "Good Afternoon,";
        else                greeting = "Good Evening,";
        tvGreeting.setText(greeting);

        // ── User name ──
        String name = prefs.getString("userName", "Athlete");
        tvUserName.setText(name + " 💪");

        // ── BMI ──
        float bmi = prefs.getFloat("userBMI", 0f);
        if (bmi > 0) {
            tvBMI.setText(String.format("%.1f", bmi));
            String status; int color;
            if      (bmi < 18.5) { status = "Underweight"; color = 0xFF2196F3; }
            else if (bmi < 25.0) { status = "Normal";       color = 0xFF4CAF50; }
            else if (bmi < 30.0) { status = "Overweight";   color = 0xFFFF9800; }
            else                 { status = "Obese";         color = 0xFFF44336; }
            tvBMIStatus.setText(status);
            tvBMIStatus.setTextColor(color);
            tvBMI.setTextColor(color);
        } else {
            tvBMI.setText("N/A");
            tvBMIStatus.setText("Set Profile");
            tvBMIStatus.setTextColor(0xFFAAAAAA);
        }

        // ── Discipline Score ──
        int score = prefs.getInt("disciplineScore", 87);
        tvDisciplineScore.setText(String.valueOf(score));

        // ── Streak ──
        int streak = prefs.getInt("workoutStreak", 12);
        tvStreak.setText("🔥 " + streak);

        // ── Today's workout ──
        String workoutTitle = prefs.getString("todayWorkout", "Upper Body Strength");
        int completed       = prefs.getInt("workoutCompleted", 2);
        int total           = prefs.getInt("workoutTotal", 6);
        int progressPercent = (total > 0) ? (completed * 100 / total) : 0;

        tvWorkoutTitle.setText(workoutTitle);
        tvWorkoutProgress.setText(completed + " / " + total + " completed");
        progressWorkout.setProgress(progressPercent);
    }

    private void setupQuickActions() {

        btnStartWorkout.setOnClickListener(v -> {
            // Increment completed count for demo
            SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
            int completed = prefs.getInt("workoutCompleted", 2);
            int total     = prefs.getInt("workoutTotal", 6);
            if (completed < total) {
                prefs.edit().putInt("workoutCompleted", completed + 1).apply();
                Toast.makeText(this, "Exercise " + (completed + 1) + " completed! 🔥", Toast.LENGTH_SHORT).show();
                loadDashboard(); // Refresh progress bar
            } else {
                Toast.makeText(this, "All exercises done today! Great job! 🏆", Toast.LENGTH_SHORT).show();
            }
        });

        cardAddWorkout.setOnClickListener(v ->
                Toast.makeText(this, "Add Workout — coming soon!", Toast.LENGTH_SHORT).show());

        cardViewProgress.setOnClickListener(v ->
                Toast.makeText(this, "Progress Tracker — coming soon!", Toast.LENGTH_SHORT).show());

        // Profile card → go to ProfileSetupActivity to edit
        cardMyProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileSetupActivity.class)));

        cardNutrition.setOnClickListener(v ->
                Toast.makeText(this, "Nutrition Log — coming soon!", Toast.LENGTH_SHORT).show());
    }

    // ── Logout helper (can be called from menu) ──────────────────────────────
    public void logout() {
        getSharedPreferences("FitLifePrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", false)
                .apply();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}