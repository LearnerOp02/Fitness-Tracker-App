package com.example.fitnessproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    // Header Views
    private TextView tvGreeting, tvUserName, tvMotivationalQuote;
    private ImageView ivProfileAvatar;
    private ImageButton btnNotification;
    private MaterialCardView profileCard, notificationCard;

    // Stats Views
    private LinearLayout statsRow;
    private MaterialCardView bmiCard, scoreCard, streakCard;
    private TextView tvBMI, tvBMIStatus, tvDisciplineScore, tvScoreLabel, tvStreak, tvStreakLabel;
    private ProgressBar progressScore;

    // Workout Views
    private MaterialCardView todayWorkoutCard;
    private TextView tvWorkoutTitle, tvWorkoutDetails, tvWorkoutProgress, tvProgressLabel;
    private ProgressBar progressWorkout;
    private Button btnStartWorkout;
    private Chip chipAdaptive;

    // Quick Actions
    private TextView tvQuickActions;
    private LinearLayout quickActionsContainer, quickActionsRow1, quickActionsRow2;
    private CardView cardAddWorkout, cardViewProgress, cardMyProfile, cardNutrition;

    // Background Circles
    private View circle1, circle2;

    private UserSessionManager sessionManager;
    private boolean backPressedOnce = false;
    private String[] motivationalQuotes = {
            "Train insane or remain the same 💪",
            "Your body can stand almost anything. It's your mind you have to convince.",
            "The only bad workout is the one that didn't happen.",
            "Fitness is not about being better than someone else. It's about being better than you used to be.",
            "Success usually comes to those who are too busy to be looking for it.",
            "The pain you feel today will be the strength you feel tomorrow.",
            "Don't stop when you're tired. Stop when you're done.",
            "Every workout is a step closer to your goal.",
            "Discipline is choosing what you want now over what you want most.",
            "Your only limit is you."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        initSessionManager();
        initViews();
        startAnimations();
        loadDashboard();
        setupListeners();
        updateMotivationalQuote();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
        updateAdaptivePlan();
    }

    private void initSessionManager() {
        try {
            if (getApplication() instanceof FitnessApplication) {
                sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
            } else {
                sessionManager = new UserSessionManager(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sessionManager = new UserSessionManager(this);
        }
    }

    private void initViews() {
        // Background circles
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);

        // Header
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserName = findViewById(R.id.tvUserName);
        tvMotivationalQuote = findViewById(R.id.tvMotivationalQuote);
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        btnNotification = findViewById(R.id.btnNotification);
        profileCard = findViewById(R.id.profileCard);
        notificationCard = findViewById(R.id.notificationCard);

        // Stats Row
        statsRow = findViewById(R.id.statsRow);

        // Stats Cards
        bmiCard = findViewById(R.id.bmiCard);
        scoreCard = findViewById(R.id.scoreCard);
        streakCard = findViewById(R.id.streakCard);
        tvBMI = findViewById(R.id.tvBMI);
        tvBMIStatus = findViewById(R.id.tvBMIStatus);
        tvDisciplineScore = findViewById(R.id.tvDisciplineScore);
        tvScoreLabel = findViewById(R.id.tvScoreLabel);
        tvStreak = findViewById(R.id.tvStreak);
        tvStreakLabel = findViewById(R.id.tvStreakLabel);
        progressScore = findViewById(R.id.progressScore);

        // Workout Card
        todayWorkoutCard = findViewById(R.id.todayWorkoutCard);
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle);
        tvWorkoutDetails = findViewById(R.id.tvWorkoutDetails);
        tvWorkoutProgress = findViewById(R.id.tvWorkoutProgress);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        progressWorkout = findViewById(R.id.progressWorkout);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);
        chipAdaptive = findViewById(R.id.chipAdaptive);

        // Quick Actions
        tvQuickActions = findViewById(R.id.tvQuickActions);
        quickActionsContainer = findViewById(R.id.quickActionsContainer);
        quickActionsRow1 = findViewById(R.id.quickActionsRow1);
        quickActionsRow2 = findViewById(R.id.quickActionsRow2);
        cardAddWorkout = findViewById(R.id.cardAddWorkout);
        cardViewProgress = findViewById(R.id.cardViewProgress);
        cardMyProfile = findViewById(R.id.cardMyProfile);
        cardNutrition = findViewById(R.id.cardNutrition);
    }

    private void startAnimations() {
        // Animate background circles
        animateBackgroundCircles();

        // Set initial visibility
        statsRow.setVisibility(View.VISIBLE);
        todayWorkoutCard.setVisibility(View.VISIBLE);
        tvQuickActions.setVisibility(View.VISIBLE);
        quickActionsContainer.setVisibility(View.VISIBLE);

        // Stats cards animation
        animateCard(bmiCard, 0);
        animateCard(scoreCard, 100);
        animateCard(streakCard, 200);

        // Workout card animation
        todayWorkoutCard.setAlpha(0f);
        todayWorkoutCard.setTranslationY(50f);
        todayWorkoutCard.setScaleX(0.95f);
        todayWorkoutCard.setScaleY(0.95f);

        ObjectAnimator workoutAlpha = ObjectAnimator.ofFloat(todayWorkoutCard, "alpha", 0f, 1f);
        ObjectAnimator workoutTranslation = ObjectAnimator.ofFloat(todayWorkoutCard, "translationY", 50f, 0f);
        ObjectAnimator workoutScaleX = ObjectAnimator.ofFloat(todayWorkoutCard, "scaleX", 0.95f, 1f);
        ObjectAnimator workoutScaleY = ObjectAnimator.ofFloat(todayWorkoutCard, "scaleY", 0.95f, 1f);

        AnimatorSet workoutAnim = new AnimatorSet();
        workoutAnim.playTogether(workoutAlpha, workoutTranslation, workoutScaleX, workoutScaleY);
        workoutAnim.setDuration(600);
        workoutAnim.setStartDelay(400);
        workoutAnim.setInterpolator(new AnticipateOvershootInterpolator(1.2f));
        workoutAnim.start();

        // Quick actions title animation
        tvQuickActions.setAlpha(0f);
        tvQuickActions.setTranslationY(30f);

        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(tvQuickActions, "alpha", 0f, 1f);
        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(tvQuickActions, "translationY", 30f, 0f);

        AnimatorSet titleAnim = new AnimatorSet();
        titleAnim.playTogether(titleAlpha, titleTranslation);
        titleAnim.setDuration(400);
        titleAnim.setStartDelay(600);
        titleAnim.start();

        // Animate first row cards
        for (int i = 0; i < quickActionsRow1.getChildCount(); i++) {
            View child = quickActionsRow1.getChildAt(i);
            child.setAlpha(0f);
            child.setTranslationY(30f);

            ObjectAnimator childAlpha = ObjectAnimator.ofFloat(child, "alpha", 0f, 1f);
            ObjectAnimator childTranslation = ObjectAnimator.ofFloat(child, "translationY", 30f, 0f);

            AnimatorSet childAnim = new AnimatorSet();
            childAnim.playTogether(childAlpha, childTranslation);
            childAnim.setDuration(400);
            childAnim.setStartDelay(700 + (i * 100));
            childAnim.start();
        }

        // Animate second row cards
        for (int i = 0; i < quickActionsRow2.getChildCount(); i++) {
            View child = quickActionsRow2.getChildAt(i);
            child.setAlpha(0f);
            child.setTranslationY(30f);

            ObjectAnimator childAlpha = ObjectAnimator.ofFloat(child, "alpha", 0f, 1f);
            ObjectAnimator childTranslation = ObjectAnimator.ofFloat(child, "translationY", 30f, 0f);

            AnimatorSet childAnim = new AnimatorSet();
            childAnim.playTogether(childAlpha, childTranslation);
            childAnim.setDuration(400);
            childAnim.setStartDelay(900 + (i * 100));
            childAnim.start();
        }
    }

    private void animateCard(View card, long delay) {
        card.setAlpha(0f);
        card.setTranslationY(50f);
        card.setScaleX(0.9f);
        card.setScaleY(0.9f);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f);
            ObjectAnimator translation = ObjectAnimator.ofFloat(card, "translationY", 50f, 0f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0.9f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0.9f, 1f);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(alpha, translation, scaleX, scaleY);
            animSet.setDuration(500);
            animSet.setInterpolator(new BounceInterpolator());
            animSet.start();
        }, delay);
    }

    private void animateBackgroundCircles() {
        // Circle 1 animation
        ObjectAnimator circle1X = ObjectAnimator.ofFloat(circle1, "translationX", 0f, 60f, -40f, 0f);
        circle1X.setDuration(5000);
        circle1X.setRepeatCount(ObjectAnimator.INFINITE);
        circle1X.setRepeatMode(ObjectAnimator.REVERSE);
        circle1X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1X.start();

        ObjectAnimator circle1Y = ObjectAnimator.ofFloat(circle1, "translationY", 0f, -50f, 40f, 0f);
        circle1Y.setDuration(6000);
        circle1Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle1Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle1Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1Y.start();

        // Circle 2 animation
        ObjectAnimator circle2X = ObjectAnimator.ofFloat(circle2, "translationX", 0f, -50f, 50f, 0f);
        circle2X.setDuration(5500);
        circle2X.setRepeatCount(ObjectAnimator.INFINITE);
        circle2X.setRepeatMode(ObjectAnimator.REVERSE);
        circle2X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2X.start();

        ObjectAnimator circle2Y = ObjectAnimator.ofFloat(circle2, "translationY", 0f, 40f, -30f, 0f);
        circle2Y.setDuration(5800);
        circle2Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle2Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle2Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2Y.start();
    }

    private void loadDashboard() {
        // Set greeting based on time
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) greeting = "Good Morning,";
        else if (hour < 17) greeting = "Good Afternoon,";
        else greeting = "Good Evening,";
        tvGreeting.setText(greeting);

        // User name
        String name = sessionManager.getUserName();
        tvUserName.setText(name);

        // BMI
        float bmi = sessionManager.getBMI();
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
        } else {
            tvBMI.setText("N/A");
            tvBMIStatus.setText("Set Profile");
        }

        // Discipline Score
        int score = sessionManager.getDisciplineScore();
        tvDisciplineScore.setText(String.valueOf(score));
        if (score >= 80) {
            tvScoreLabel.setText("Excellent");
            tvScoreLabel.setTextColor(0xFF4CAF50);
        } else if (score >= 60) {
            tvScoreLabel.setText("Good");
            tvScoreLabel.setTextColor(0xFFFFB300);
        } else if (score >= 40) {
            tvScoreLabel.setText("Fair");
            tvScoreLabel.setTextColor(0xFFFF9800);
        } else {
            tvScoreLabel.setText("Needs Work");
            tvScoreLabel.setTextColor(0xFFF44336);
        }
        if (progressScore != null) {
            progressScore.setProgress(score);
        }

        // Streak
        int streak = sessionManager.getWorkoutStreak();
        tvStreak.setText(String.valueOf(streak));
        tvStreakLabel.setText("🔥 " + streak + " Days");

        // Workout progress
        String workoutTitle = sessionManager.getTodayWorkout();
        int completed = sessionManager.getWorkoutCompleted();
        int total = sessionManager.getWorkoutTotal();
        int progressPercent = (total > 0) ? (completed * 100 / total) : 0;

        tvWorkoutTitle.setText(workoutTitle);
        tvWorkoutDetails.setText(getWorkoutDetails(workoutTitle));
        tvWorkoutProgress.setText(completed + "/" + total);
        progressWorkout.setProgress(progressPercent);
    }

    private String getWorkoutDetails(String workoutTitle) {
        switch (workoutTitle) {
            case "Upper Body Strength":
                return "6 Exercises  •  45 min  •  Intermediate";
            case "Lower Body Focus":
                return "5 Exercises  •  40 min  •  Beginner";
            case "Cardio Blast":
                return "8 Exercises  •  30 min  •  Advanced";
            case "Full Body Workout":
                return "7 Exercises  •  50 min  •  Intermediate";
            default:
                return "5 Exercises  •  35 min  •  Beginner";
        }
    }

    private void updateMotivationalQuote() {
        Random random = new Random();
        int index = random.nextInt(motivationalQuotes.length);
        tvMotivationalQuote.setText(motivationalQuotes[index]);

        // Change quote every 10 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int newIndex = random.nextInt(motivationalQuotes.length);
            tvMotivationalQuote.setText(motivationalQuotes[newIndex]);
        }, 10000);
    }

    private void updateAdaptivePlan() {
        // Simulated data - replace with actual session manager methods
        int missedWorkouts = sessionManager.getMissedWorkoutsLast7Days();
        int completionRate = (int) sessionManager.getCompletionRateLast2Weeks();

        String adaptiveMessage;
        if (missedWorkouts >= 3) {
            adaptiveMessage = "🔄 Plan adapted: Intensity reduced by 20%";
            chipAdaptive.setText(adaptiveMessage);
            chipAdaptive.setVisibility(View.VISIBLE);
        } else if (completionRate > 85) {
            adaptiveMessage = "⚡ Plan adapted: Intensity increased by 10%";
            chipAdaptive.setText(adaptiveMessage);
            chipAdaptive.setVisibility(View.VISIBLE);
        } else {
            chipAdaptive.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        // Profile click
        profileCard.setOnClickListener(v -> {
            animateClick(v);
            Intent intent = new Intent(HomeActivity.this, ProfileSetupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Notification click
        notificationCard.setOnClickListener(v -> {
            animateClick(v);
            Intent intent = new Intent(HomeActivity.this, NotificationsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Start workout button
        btnStartWorkout.setOnClickListener(v -> {
            animateClick(v);
            int completed = sessionManager.getWorkoutCompleted();
            int total = sessionManager.getWorkoutTotal();

            if (completed < total) {
                // Simulate workout completion
                sessionManager.incrementWorkoutCompleted();
                sessionManager.incrementDisciplineScore(2);

                // Show celebration animation
                showWorkoutCompletedAnimation();

                Toast.makeText(this, "Exercise " + (completed + 1) + " completed! 🔥", Toast.LENGTH_SHORT).show();
                loadDashboard();

                // Update adaptive plan
                updateAdaptivePlan();
            } else {
                Toast.makeText(this, "All exercises done today! Great job! 🏆", Toast.LENGTH_SHORT).show();
            }
        });

        // Quick Actions
        cardAddWorkout.setOnClickListener(v -> {
            animateClick(v);
            Intent intent = new Intent(HomeActivity.this, AddWorkoutActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        cardViewProgress.setOnClickListener(v -> {
            animateClick(v);
            Intent intent = new Intent(HomeActivity.this, MonthlyProgressActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        cardMyProfile.setOnClickListener(v -> {
            animateClick(v);
            Intent intent = new Intent(HomeActivity.this, LogoutConfirmActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        cardNutrition.setOnClickListener(v -> {
            animateClick(v);
            Toast.makeText(this, "🥗 Nutrition tracking coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Stats cards click listeners for more info
        bmiCard.setOnClickListener(v -> {
            animateClick(v);
            Toast.makeText(this, "BMI: " + tvBMI.getText() + " - " + tvBMIStatus.getText(), Toast.LENGTH_SHORT).show();
        });

        scoreCard.setOnClickListener(v -> {
            animateClick(v);
            Toast.makeText(this, "Discipline Score: " + tvDisciplineScore.getText() + " - " + tvScoreLabel.getText(), Toast.LENGTH_SHORT).show();
        });

        streakCard.setOnClickListener(v -> {
            animateClick(v);
            Toast.makeText(this, "Current Streak: " + tvStreak.getText() + " days", Toast.LENGTH_SHORT).show();
        });
    }

    private void animateClick(View view) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start())
                .start();
    }

    private void showWorkoutCompletedAnimation() {
        // Animate progress bar
        int newProgress = (sessionManager.getWorkoutCompleted() * 100 / sessionManager.getWorkoutTotal());
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(progressWorkout, "progress",
                progressWorkout.getProgress(), newProgress);
        progressAnim.setDuration(500);
        progressAnim.setInterpolator(new DecelerateInterpolator());
        progressAnim.start();

        // Bounce the workout card
        todayWorkoutCard.animate()
                .scaleX(1.02f)
                .scaleY(1.02f)
                .setDuration(150)
                .withEndAction(() ->
                        todayWorkoutCard.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .start())
                .start();

        // Update workout progress text
        tvWorkoutProgress.setText(sessionManager.getWorkoutCompleted() + "/" + sessionManager.getWorkoutTotal());
    }

//    @Override
//    public void onBackPressed() {
//        if (backPressedOnce) {
//            super.onBackPressed();
//            finishAffinity();
//            return;
//        }
//
//        backPressedOnce = true;
//        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler(Looper.getMainLooper()).postDelayed(() -> backPressedOnce = false, 2000);
//    }
}