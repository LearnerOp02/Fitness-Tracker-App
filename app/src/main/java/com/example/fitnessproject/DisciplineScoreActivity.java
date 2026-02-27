package com.example.fitnessproject;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

public class DisciplineScoreActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ScoreRingView scoreRingView;
    private TextView tvScoreGrade, tvStreakCount, tvCompletionRate,
            tvTotalLogged, tvScoreTips;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discipline_score);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
        initViews();
        loadData();
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        scoreRingView = findViewById(R.id.scoreRingView);
        tvScoreGrade = findViewById(R.id.tvScoreGrade);
        tvStreakCount = findViewById(R.id.tvStreakCount);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);
        tvTotalLogged = findViewById(R.id.tvTotalLogged);
        tvScoreTips = findViewById(R.id.tvScoreTips);
    }

    private void loadData() {
        int score = sessionManager.getDisciplineScore();
        int streak = sessionManager.getWorkoutStreak();

        int total = 0;
        try {
            total = new JSONArray(sessionManager.getWorkoutHistory()).length();
        } catch (Exception ignored) {
        }

        int completed = sessionManager.getWorkoutCompleted();
        int totalPlan = sessionManager.getWorkoutTotal();
        int rate = totalPlan > 0 ? (completed * 100 / totalPlan) : 0;

        scoreRingView.setScore(score);

        String grade;
        if (score >= 90) grade = "Grade: S  🌟";
        else if (score >= 80) grade = "Grade: A  🏆";
        else if (score >= 70) grade = "Grade: B  💪";
        else if (score >= 60) grade = "Grade: C  👍";
        else grade = "Grade: D  — Keep going!";
        tvScoreGrade.setText(grade);

        tvStreakCount.setText(String.valueOf(streak));
        tvCompletionRate.setText(rate + "%");
        tvTotalLogged.setText(String.valueOf(total));

        String tips;
        if (score >= 80) {
            tips = "• Excellent consistency! Stay consistent.\n" +
                    "• Challenge yourself with harder workouts.\n" +
                    "• Consider helping others in their journey.";
        } else if (score >= 60) {
            tips = "• Log a workout every day to build habit.\n" +
                    "• Complete at least 5/7 days each week.\n" +
                    "• Maintain your streak — don't break the chain!";
        } else {
            tips = "• Start small — even 15 min counts!\n" +
                    "• Set a daily reminder for your workout.\n" +
                    "• Log every activity, no matter how small.\n" +
                    "• Aim for 3 workouts/week minimum.";
        }
        tvScoreTips.setText(tips);
    }
}