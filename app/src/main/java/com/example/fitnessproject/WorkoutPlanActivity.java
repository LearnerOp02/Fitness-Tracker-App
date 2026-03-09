package com.example.fitnessproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

public class WorkoutPlanActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView profileCard, adaptiveCard;
    private TextView tvPlanTitle, tvProfileSummary, tvAdaptiveReason;
    private Chip chipAdaptiveBadge;
    private LinearLayout layoutPlanDays;

    private boolean isAdaptive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_plan);

        isAdaptive = getIntent().getBooleanExtra("adaptive", false);

        initViews();
        setupToolbar();
        buildPlan();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
        profileCard = findViewById(R.id.profileCard);
//        adaptiveCard = findViewById(R.id.adaptiveCard);
        tvPlanTitle = findViewById(R.id.tvPlanTitle);
        tvProfileSummary = findViewById(R.id.tvProfileSummary);
        tvAdaptiveReason = findViewById(R.id.tvAdaptiveReason);
//        chipAdaptiveBadge = findViewById(R.id.chipAdaptiveBadge);
        layoutPlanDays = findViewById(R.id.layoutPlanDays);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, isAdaptive ? "Adaptive Plan" : "Workout Plan", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(profileCard, 0);
        if (isAdaptive) {
            animateCard(adaptiveCard, 150);
        }
        // Plan days will be animated as they're added
    }

    private void buildPlan() {
        float bmi = sessionManager.getBMI();
        String goal = sessionManager.getUserGoal();
        String level = sessionManager.getUserActivityLevel();
        int streak = sessionManager.getWorkoutStreak();
        int score = sessionManager.getDisciplineScore();

        tvProfileSummary.setText(String.format("BMI: %.1f  |  Goal: %s  |  Level: %s", bmi, goal, level));

        if (isAdaptive) {
            tvPlanTitle.setText("Adaptive Workout Plan");
            chipAdaptiveBadge.setVisibility(View.VISIBLE);
            adaptiveCard.setVisibility(View.VISIBLE);

            String reason;
            if (streak == 0) {
                reason = "Plan lightened — no workouts logged recently. Start slow!";
            } else if (score < 50) {
                reason = "Intensity reduced — consistency score below 50%. Build the habit first.";
            } else if (streak >= 7) {
                reason = "Plan intensified — 7+ day streak detected! You're on fire 🔥";
            } else {
                reason = "Plan adapted to your current activity level and progress.";
            }
            tvAdaptiveReason.setText(reason);
        } else {
            tvPlanTitle.setText("Your Workout Plan");
        }

        String[][] plan = generatePlan(bmi, goal, level, isAdaptive, score);
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (int i = 0; i < days.length; i++) {
            addDayCard(days[i], plan[i][0], plan[i][1], plan[i][2], i * 100);
        }
    }

    private String[][] generatePlan(float bmi, String goal, String level,
                                    boolean adaptive, int score) {
        float factor = adaptive ? (score < 50 ? 0.7f : (score >= 80 ? 1.2f : 1.0f)) : 1.0f;

        boolean isOverweight = bmi >= 25;
        boolean isMuscle = goal.toLowerCase().contains("muscle") || goal.toLowerCase().contains("strength");
        boolean isCardio = goal.toLowerCase().contains("weight") || goal.toLowerCase().contains("slim");

        int dur = (int) (30 * factor);
        int durHard = (int) (45 * factor);
        String rest = "Rest & Stretching — 15 min";

        if (isCardio || isOverweight) {
            return new String[][]{
                    {"Brisk Walk / Jog", dur + " min", "Moderate pace"},
                    {"Cycling or Elliptical", durHard + " min", "Interval: 1 min fast / 2 min slow"},
                    {"Bodyweight HIIT", (int) (20 * factor) + " min", "3 sets × 10 reps"},
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

    private void addDayCard(String day, String exercise, String duration, String detail, long delay) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(getResources().getColor(R.color.surface));
        card.setRadius(40f);
        card.setCardElevation(4f);
        card.setStrokeColor(getResources().getColor(R.color.primary));
        card.setStrokeWidth(1);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(40, 36, 40, 36);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvDay = new TextView(this);
        tvDay.setText(day.substring(0, 3).toUpperCase());
        tvDay.setTextColor(getResources().getColor(R.color.primary));
        tvDay.setTextSize(12f);
        tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
        tvDay.setMinWidth(120);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colP = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        col.setLayoutParams(colP);

        TextView tvExercise = new TextView(this);
        tvExercise.setText(exercise);
        tvExercise.setTextColor(Color.WHITE);
        tvExercise.setTextSize(14f);
        tvExercise.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDetail = new TextView(this);
        tvDetail.setText(duration + "  ·  " + detail);
        tvDetail.setTextColor(getResources().getColor(R.color.text_secondary));
        tvDetail.setTextSize(11f);
        tvDetail.setLineSpacing(4f, 1f);

        col.addView(tvExercise);
        col.addView(tvDetail);

        row.addView(tvDay);
        row.addView(col);
        card.addView(row);
        layoutPlanDays.addView(card);

        // Animate the card
        card.setAlpha(0f);
        card.setTranslationY(50f);
        handler.postDelayed(() -> {
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .start();
        }, delay + 200);
    }
}