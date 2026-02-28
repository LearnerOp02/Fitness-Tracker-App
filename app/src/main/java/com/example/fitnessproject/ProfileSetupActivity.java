package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText etAge, etHeight, etWeight;
    private RadioGroup rgGender;
    private Spinner spinnerProfileGoal, spinnerActivityLevel;
    private Button btnSaveProfile;
    private TextView tvBMIValue, tvBMICategory;
    private ImageButton btnBack;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);

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

        initViews();
        setupSpinners();
        setupBMIListener();
        setupListeners();
        loadExistingProfile();
    }

    private void initViews() {
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        rgGender = findViewById(R.id.rgGender);
        spinnerProfileGoal = findViewById(R.id.spinnerProfileGoal);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        tvBMIValue = findViewById(R.id.tvBMIValue);
        tvBMICategory = findViewById(R.id.tvBMICategory);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupSpinners() {
        String[] goals = {
                "Select Fitness Goal", "Lose Weight", "Build Muscle",
                "Improve Endurance", "Stay Active & Healthy",
                "Increase Flexibility", "Sports Performance"
        };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, goals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerProfileGoal != null) spinnerProfileGoal.setAdapter(goalAdapter);

        String[] levels = {
                "Select Activity Level",
                "Sedentary (little or no exercise)",
                "Lightly Active (1-3 days/week)",
                "Moderately Active (3-5 days/week)",
                "Very Active (6-7 days/week)",
                "Super Active (twice/day)"
        };
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerActivityLevel != null) spinnerActivityLevel.setAdapter(levelAdapter);
    }

    private void loadExistingProfile() {
        try {
            String height = sessionManager.getUserHeight();
            String weight = sessionManager.getUserWeight();
            String age = sessionManager.getUserAge();
            String gender = sessionManager.getUserGender();
            String goal = sessionManager.getUserGoal();
            String level = sessionManager.getUserActivityLevel();

            if (!height.isEmpty() && etHeight != null) etHeight.setText(height);
            if (!weight.isEmpty() && etWeight != null) etWeight.setText(weight);
            if (!age.isEmpty() && etAge != null) etAge.setText(age);

            if (rgGender != null) {
                if (gender.equals("Male")) rgGender.check(R.id.rbMale);
                else if (gender.equals("Female")) rgGender.check(R.id.rbFemale);
                else if (gender.equals("Other")) rgGender.check(R.id.rbOther);
            }

            if (spinnerProfileGoal != null) {
                for (int i = 0; i < spinnerProfileGoal.getCount(); i++) {
                    if (spinnerProfileGoal.getItemAtPosition(i).toString().equals(goal)) {
                        spinnerProfileGoal.setSelection(i);
                        break;
                    }
                }
            }

            if (spinnerActivityLevel != null) {
                for (int i = 0; i < spinnerActivityLevel.getCount(); i++) {
                    if (spinnerActivityLevel.getItemAtPosition(i).toString().equals(level)) {
                        spinnerActivityLevel.setSelection(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBMIListener() {
        if (etHeight != null && etWeight != null) {
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int i, int c, int a) {}

                @Override
                public void onTextChanged(CharSequence s, int i, int b, int c) {}

                @Override
                public void afterTextChanged(Editable s) {
                    calculateBMI();
                }
            };
            etHeight.addTextChangedListener(watcher);
            etWeight.addTextChangedListener(watcher);
        }
    }

    private void calculateBMI() {
        if (etHeight == null || etWeight == null || tvBMIValue == null || tvBMICategory == null) return;
        String h = etHeight.getText().toString().trim();
        String w = etWeight.getText().toString().trim();

        if (h.isEmpty() || w.isEmpty()) {
            tvBMIValue.setText("--");
            tvBMICategory.setText("");
            return;
        }
        try {
            double heightM = Double.parseDouble(h) / 100.0;
            double weightK = Double.parseDouble(w);
            if (heightM <= 0 || weightK <= 0) return;

            double bmi = weightK / (heightM * heightM);
            tvBMIValue.setText(String.format("%.1f", bmi));

            String cat;
            int color;
            if (bmi < 18.5) {
                cat = "Underweight";
                color = 0xFF2196F3;
            } else if (bmi < 25.0) {
                cat = "Normal";
                color = 0xFF4CAF50;
            } else if (bmi < 30.0) {
                cat = "Overweight";
                color = 0xFFFF9800;
            } else {
                cat = "Obese";
                color = 0xFFF44336;
            }

            tvBMICategory.setText(cat);
            tvBMICategory.setTextColor(color);
            tvBMIValue.setTextColor(color);

        } catch (NumberFormatException ignored) {
        }
    }

    private void setupListeners() {
        if (btnSaveProfile != null) btnSaveProfile.setOnClickListener(v -> saveProfile());

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Exit Profile Setup")
                        .setMessage("Your profile is not complete. Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (sessionManager.isLoggedIn()) {
                                Intent intent = new Intent(this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            finishAffinity();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    private void saveProfile() {
        if (etAge == null || etHeight == null || etWeight == null ||
                spinnerProfileGoal == null || spinnerActivityLevel == null || rgGender == null) {
            Toast.makeText(this, "UI initialization error", Toast.LENGTH_SHORT).show();
            return;
        }

        String age = etAge.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();

        if (TextUtils.isEmpty(age)) {
            etAge.setError("Age is required");
            etAge.requestFocus();
            return;
        }
        int ageInt = Integer.parseInt(age);
        if (ageInt < 10 || ageInt > 100) {
            etAge.setError("Enter valid age (10–100)");
            etAge.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(height)) {
            etHeight.setError("Height is required");
            etHeight.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(weight)) {
            etWeight.setError("Weight is required");
            etWeight.requestFocus();
            return;
        }
        if (spinnerProfileGoal.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a fitness goal", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerActivityLevel.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select your activity level", Toast.LENGTH_SHORT).show();
            return;
        }

        int genderId = rgGender.getCheckedRadioButtonId();
        RadioButton genderBtn = findViewById(genderId);
        String gender = genderBtn != null ? genderBtn.getText().toString() : "Male";

        double heightM = Double.parseDouble(height) / 100.0;
        double bmi = Double.parseDouble(weight) / (heightM * heightM);

        // Save previous month data if first time
        if (!sessionManager.isProfileComplete()) {
            sessionManager.savePrevMonthData(Float.parseFloat(weight), (float) bmi);
        }

        sessionManager.saveProfile(age, height, weight, gender,
                spinnerProfileGoal.getSelectedItem().toString(),
                spinnerActivityLevel.getSelectedItem().toString(), (float) bmi);

        Toast.makeText(this, "Profile saved! Let's go! 🚀", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileSetupActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }
}