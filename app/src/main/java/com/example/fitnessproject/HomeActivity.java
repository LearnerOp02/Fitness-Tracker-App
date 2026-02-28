package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting, tvUserName;
    private TextView tvBMI, tvBMIStatus;
    private TextView tvDisciplineScore, tvStreak;
    private TextView tvWorkoutTitle, tvWorkoutProgress;
    private ProgressBar progressWorkout;
    private Button btnStartWorkout;
    private CardView cardAddWorkout, cardViewProgress, cardMyProfile, cardNutrition;
    private ImageButton btnNotification;
    private UserSessionManager sessionManager;

    private boolean backPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
        initViews();
        loadDashboard();
        setupQuickActions();
        setupLogout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserName = findViewById(R.id.tvUserName);
        tvBMI = findViewById(R.id.tvBMI);
        tvBMIStatus = findViewById(R.id.tvBMIStatus);
        tvDisciplineScore = findViewById(R.id.tvDisciplineScore);
        tvStreak = findViewById(R.id.tvStreak);
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle);
        tvWorkoutProgress = findViewById(R.id.tvWorkoutProgress);
        progressWorkout = findViewById(R.id.progressWorkout);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);
        cardAddWorkout = findViewById(R.id.cardAddWorkout);
        cardViewProgress = findViewById(R.id.cardViewProgress);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        cardNutrition = findViewById(R.id.cardNutrition);
        btnNotification = findViewById(R.id.btnNotification);
    }

    private void loadDashboard() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) greeting = "Good Morning,";
        else if (hour < 17) greeting = "Good Afternoon,";
        else greeting = "Good Evening,";
        if (tvGreeting != null) tvGreeting.setText(greeting);

        String name = sessionManager.getUserName();
        if (tvUserName != null) tvUserName.setText(name + " 💪");

        float bmi = sessionManager.getBMI();
        if (tvBMI != null && tvBMIStatus != null) {
            if (bmi > 0) {
                tvBMI.setText(String.format("%.1f", bmi));
                String status;
                int color;
                if (bmi < 18.5) {
                    status = "Underweight";
                    color = 0xFF2196F3;
                } else if (bmi < 25.0) {
                    status = "Normal";
                    color = 0xFF4CAF50;
                } else if (bmi < 30.0) {
                    status = "Overweight";
                    color = 0xFFFF9800;
                } else {
                    status = "Obese";
                    color = 0xFFF44336;
                }
                tvBMIStatus.setText(status);
                tvBMIStatus.setTextColor(color);
                tvBMI.setTextColor(color);
            } else {
                tvBMI.setText("N/A");
                tvBMIStatus.setText("Set Profile");
                tvBMIStatus.setTextColor(0xFFAAAAAA);
            }
        }

        int score = sessionManager.getDisciplineScore();
        if (tvDisciplineScore != null) tvDisciplineScore.setText(String.valueOf(score));

        int streak = sessionManager.getWorkoutStreak();
        if (tvStreak != null) tvStreak.setText("🔥 " + streak);

        String workoutTitle = sessionManager.getTodayWorkout();
        int completed = sessionManager.getWorkoutCompleted();
        int total = sessionManager.getWorkoutTotal();
        int progressPercent = (total > 0) ? (completed * 100 / total) : 0;

        if (tvWorkoutTitle != null) tvWorkoutTitle.setText(workoutTitle);
        if (tvWorkoutProgress != null) tvWorkoutProgress.setText(completed + " / " + total + " completed");
        if (progressWorkout != null) progressWorkout.setProgress(progressPercent);
    }

    private void setupQuickActions() {
        if (btnStartWorkout != null) {
            btnStartWorkout.setOnClickListener(v -> {
                int completed = sessionManager.getWorkoutCompleted();
                int total = sessionManager.getWorkoutTotal();
                if (completed < total) {
                    sessionManager.incrementWorkoutCompleted();
                    sessionManager.incrementDisciplineScore(2);
                    Toast.makeText(this, "Exercise " + (completed + 1) + " completed! 🔥", Toast.LENGTH_SHORT).show();
                    loadDashboard();
                } else {
                    Toast.makeText(this, "All exercises done today! Great job! 🏆", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (cardAddWorkout != null) {
            cardAddWorkout.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, AddWorkoutActivity.class)));
        }

        if (cardViewProgress != null) {
            cardViewProgress.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MonthlyProgressActivity.class);
                startActivity(intent);
            });
        }

        if (cardMyProfile != null) {
            cardMyProfile.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileSetupActivity.class);
                startActivity(intent);
            });
        }

        if (cardNutrition != null) {
            cardNutrition.setOnClickListener(v ->
                    Toast.makeText(this, "Nutrition Log — coming soon!", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupLogout() {
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }

    public void logout() {
        sessionManager.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }

        backPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> backPressedOnce = false, 2000);
    }
}