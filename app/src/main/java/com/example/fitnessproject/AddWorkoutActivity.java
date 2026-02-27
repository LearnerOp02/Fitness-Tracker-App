package com.example.fitnessproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddWorkoutActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTodayDate;
    private Spinner spinnerExerciseType;
    private EditText etWorkoutName, etDuration, etNotes;
    private RadioGroup rgIntensity;
    private TextView tvCaloriesEstimate;
    private Button btnSaveWorkout;
    private UserSessionManager sessionManager;

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

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
        initViews();
        setupSpinner();
        setupCalorieEstimator();
        setupListeners();
        showTodayDate();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        spinnerExerciseType = findViewById(R.id.spinnerExerciseType);
        etWorkoutName = findViewById(R.id.etWorkoutName);
        etDuration = findViewById(R.id.etDuration);
        etNotes = findViewById(R.id.etNotes);
        rgIntensity = findViewById(R.id.rgIntensity);
        tvCaloriesEstimate = findViewById(R.id.tvCaloriesEstimate);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
    }

    private void showTodayDate() {
        String date = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault())
                .format(new Date());
        tvTodayDate.setText("Today — " + date);
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
            public void beforeTextChanged(CharSequence s, int i, int c, int a) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int b, int c) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCalories();
            }
        };
        etDuration.addTextChangedListener(watcher);

        rgIntensity.setOnCheckedChangeListener((group, checkedId) -> updateCalories());
    }

    private void updateCalories() {
        String durStr = etDuration.getText().toString().trim();
        if (durStr.isEmpty()) {
            tvCaloriesEstimate.setText("— kcal");
            return;
        }
        int duration = Integer.parseInt(durStr);
        int multiplier;
        int checkedId = rgIntensity.getCheckedRadioButtonId();
        if (checkedId == R.id.rbLow) multiplier = CALORIES_PER_MIN[0];
        else if (checkedId == R.id.rbHigh) multiplier = CALORIES_PER_MIN[2];
        else multiplier = CALORIES_PER_MIN[1];
        tvCaloriesEstimate.setText("~" + (duration * multiplier) + " kcal");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    private void saveWorkout() {
        if (spinnerExerciseType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select an exercise type", Toast.LENGTH_SHORT).show();
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

        int duration = Integer.parseInt(durStr);
        if (duration <= 0 || duration > 600) {
            etDuration.setError("Enter a valid duration (1-600 min)");
            etDuration.requestFocus();
            return;
        }

        String exerciseType = spinnerExerciseType.getSelectedItem().toString();
        String notes = etNotes.getText().toString().trim();
        String calories = tvCaloriesEstimate.getText().toString();
        String intensity = getIntensityLabel();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String displayDate = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault()).format(new Date());

        String historyJson = sessionManager.getWorkoutHistory();

        try {
            JSONArray history = new JSONArray(historyJson);
            JSONObject entry = new JSONObject();
            entry.put("name", name);
            entry.put("type", exerciseType);
            entry.put("duration", duration);
            entry.put("intensity", intensity);
            entry.put("calories", calories);
            entry.put("notes", notes);
            entry.put("date", date);
            entry.put("displayDate", displayDate);
            history.put(entry);

            sessionManager.setWorkoutHistory(history.toString());
            sessionManager.incrementDisciplineScore(2);
            sessionManager.incrementWorkoutStreak();
            sessionManager.setTodayWorkout(name);

            Toast.makeText(this, "Workout saved! 🔥", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error saving workout. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getIntensityLabel() {
        int id = rgIntensity.getCheckedRadioButtonId();
        if (id == R.id.rbLow) return "Low";
        if (id == R.id.rbHigh) return "High";
        return "Medium";
    }
}