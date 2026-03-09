package com.example.fitnessproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddWorkoutActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView workoutCard;
    private TextView tvTodayDate, tvCaloriesEstimate;
    private Spinner spinnerExerciseType;
    private EditText etWorkoutName, etDuration, etNotes;
    private MaterialButtonToggleGroup intensityToggleGroup;
    private Button btnSaveWorkout;

    private static final String[] EXERCISE_TYPES = {
            "Select Exercise Type",
            "Cardio", "Strength Training", "Yoga", "HIIT",
            "Cycling", "Swimming", "Running", "Walking",
            "Stretching", "Sports", "Other"
    };

    private static final int[] CALORIES_PER_MIN = {4, 7, 10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_workout);

        initViews();
        setupToolbar();
        setupSpinner();
        setupCalorieEstimator();
        setupListeners();
        showTodayDate();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
        workoutCard = findViewById(R.id.workoutCard);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        spinnerExerciseType = findViewById(R.id.spinnerExerciseType);
        etWorkoutName = findViewById(R.id.etWorkoutName);
        etDuration = findViewById(R.id.etDuration);
        etNotes = findViewById(R.id.etNotes);
        intensityToggleGroup = findViewById(R.id.intensityToggleGroup);
        tvCaloriesEstimate = findViewById(R.id.tvCaloriesEstimate);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Add Workout", true);
    }

    private void showTodayDate() {
        String date = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
                .format(new Date());
        tvTodayDate.setText(date);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, EXERCISE_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExerciseType.setAdapter(adapter);
    }

    private void setupCalorieEstimator() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCalories();
            }
        };
        etDuration.addTextChangedListener(watcher);

        intensityToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) updateCalories();
        });
    }

    private void updateCalories() {
        String durStr = etDuration.getText().toString().trim();
        if (durStr.isEmpty()) {
            tvCaloriesEstimate.setText("— kcal");
            return;
        }

        try {
            int duration = Integer.parseInt(durStr);
            int multiplier;
            int checkedId = intensityToggleGroup.getCheckedButtonId();

            if (checkedId == R.id.btnLow) multiplier = CALORIES_PER_MIN[0];
            else if (checkedId == R.id.btnHigh) multiplier = CALORIES_PER_MIN[2];
            else multiplier = CALORIES_PER_MIN[1];

            tvCaloriesEstimate.setText((duration * multiplier) + " kcal");
        } catch (NumberFormatException e) {
            tvCaloriesEstimate.setText("— kcal");
        }
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(workoutCard, 0);
    }

    private void setupListeners() {
        btnSaveWorkout.setOnClickListener(v -> {
            animateClick(v);
            saveWorkout();
        });
    }

    private void saveWorkout() {
        if (spinnerExerciseType.getSelectedItemPosition() == 0) {
            showToast("Please select an exercise type");
            return;
        }

        String name = etWorkoutName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etWorkoutName.setError("Enter workout name");
            etWorkoutName.requestFocus();
            return;
        }

        String durStr = etDuration.getText().toString().trim();
        if (TextUtils.isEmpty(durStr)) {
            etDuration.setError("Enter duration");
            etDuration.requestFocus();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durStr);
            if (duration <= 0 || duration > 600) {
                etDuration.setError("Enter valid duration (1-600 min)");
                etDuration.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etDuration.setError("Invalid duration");
            etDuration.requestFocus();
            return;
        }

        btnSaveWorkout.setEnabled(false);
        btnSaveWorkout.setText("SAVING...");

        handler.postDelayed(() -> {
            try {
                String exerciseType = spinnerExerciseType.getSelectedItem().toString();
                String notes = etNotes.getText().toString().trim();
                String intensity = getIntensityLabel();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String displayDate = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(new Date());

                JSONArray history = new JSONArray(sessionManager.getWorkoutHistory());
                JSONObject entry = new JSONObject();

                entry.put("name", name);
                entry.put("type", exerciseType);
                entry.put("duration", duration);
                entry.put("intensity", intensity);
                entry.put("calories", tvCaloriesEstimate.getText().toString());
                entry.put("notes", notes);
                entry.put("date", date);
                entry.put("displayDate", displayDate);

                history.put(entry);
                sessionManager.setWorkoutHistory(history.toString());

                // Update stats
                sessionManager.incrementDisciplineScore(2);
                sessionManager.updateStreakBasedOnLastWorkout();
                sessionManager.setTodayWorkout(name);

                showToast("Workout saved! 🔥");

                // Animate exit
                workoutCard.animate()
                        .alpha(0f)
                        .translationY(-100f)
                        .setDuration(300)
                        .withEndAction(() -> finish())
                        .start();

            } catch (Exception e) {
                e.printStackTrace();
                btnSaveWorkout.setEnabled(true);
                btnSaveWorkout.setText("SAVE WORKOUT");
                showToast("Error saving workout");
            }
        }, 1500);
    }

    private String getIntensityLabel() {
        int id = intensityToggleGroup.getCheckedButtonId();
        if (id == R.id.btnLow) return "Low";
        if (id == R.id.btnHigh) return "High";
        return "Medium";
    }
}