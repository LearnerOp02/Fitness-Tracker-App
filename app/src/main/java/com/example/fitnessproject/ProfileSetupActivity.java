package com.example.fitnessproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileSetupActivity extends AppCompatActivity {

    private TextInputLayout ageLayout, heightLayout, weightLayout;
    private EditText etAge, etHeight, etWeight;
    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnMale, btnFemale, btnOther;
    private Spinner spinnerProfileGoal, spinnerActivityLevel;
    private Button btnSaveProfile;
    private TextView tvBMIValue, tvBMICategory;
    private FloatingActionButton btnEditPhoto;
    private Toolbar toolbar;
    private View circle1, circle2;
    private MaterialCardView photoCard, personalCard, bodyCard, fitnessCard;

    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);

        initSessionManager();
        initViews();
        setupSpinners();
        setupListeners();
        setupBMIListener();
        loadExistingProfile();
        startAnimations();
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

        // Cards
        photoCard = findViewById(R.id.photoCard);
        personalCard = findViewById(R.id.personalCard);
        bodyCard = findViewById(R.id.bodyCard);
        fitnessCard = findViewById(R.id.fitnessCard);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> showExitDialog());

        // TextInputLayouts
        ageLayout = findViewById(R.id.ageLayout);
        heightLayout = findViewById(R.id.heightLayout);
        weightLayout = findViewById(R.id.weightLayout);

        // EditTexts
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);

        // Gender toggle
        toggleGroup = findViewById(R.id.toggleGroup);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        btnOther = findViewById(R.id.btnOther);

        // Spinners
        spinnerProfileGoal = findViewById(R.id.spinnerProfileGoal);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);

        // Buttons
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);

        // TextViews
        tvBMIValue = findViewById(R.id.tvBMIValue);
        tvBMICategory = findViewById(R.id.tvBMICategory);
    }

    private void setupSpinners() {
        // Goals spinner
        String[] goals = {
                "Select Fitness Goal",
                "Lose Weight",
                "Build Muscle",
                "Improve Endurance",
                "Stay Active & Healthy",
                "Increase Flexibility",
                "Sports Performance"
        };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, goals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfileGoal.setAdapter(goalAdapter);

        // Activity levels spinner
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
        spinnerActivityLevel.setAdapter(levelAdapter);
    }

    private void startAnimations() {
        // Animate background circles
        animateBackgroundCircles();

        // Hide cards initially
        photoCard.setVisibility(View.VISIBLE);
        personalCard.setVisibility(View.VISIBLE);
        bodyCard.setVisibility(View.VISIBLE);
        fitnessCard.setVisibility(View.VISIBLE);
        btnSaveProfile.setVisibility(View.VISIBLE);

        // Set initial states for animation
        photoCard.setAlpha(0f);
        photoCard.setTranslationY(50f);
        personalCard.setAlpha(0f);
        personalCard.setTranslationY(50f);
        bodyCard.setAlpha(0f);
        bodyCard.setTranslationY(50f);
        fitnessCard.setAlpha(0f);
        fitnessCard.setTranslationY(50f);
        btnSaveProfile.setAlpha(0f);
        btnSaveProfile.setTranslationY(50f);

        // Animate cards sequentially
        animateCard(photoCard, 0);
        animateCard(personalCard, 150);
        animateCard(bodyCard, 300);
        animateCard(fitnessCard, 450);

        // Animate button last
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(btnSaveProfile, "alpha", 0f, 1f);
            ObjectAnimator translation = ObjectAnimator.ofFloat(btnSaveProfile, "translationY", 50f, 0f);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(alpha, translation);
            animSet.setDuration(600);
            animSet.setInterpolator(new DecelerateInterpolator());
            animSet.start();
        }, 600);
    }

    private void animateCard(View card, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f);
            ObjectAnimator translation = ObjectAnimator.ofFloat(card, "translationY", 50f, 0f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0.95f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0.95f, 1f);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(alpha, translation, scaleX, scaleY);
            animSet.setDuration(600);
            animSet.setInterpolator(new AnticipateOvershootInterpolator(1.2f));
            animSet.start();
        }, delay);
    }

    private void animateBackgroundCircles() {
        // Circle 1 animation
        ObjectAnimator circle1X = ObjectAnimator.ofFloat(circle1, "translationX", 0f, 50f, -30f, 0f);
        circle1X.setDuration(4000);
        circle1X.setRepeatCount(ObjectAnimator.INFINITE);
        circle1X.setRepeatMode(ObjectAnimator.REVERSE);
        circle1X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1X.start();

        ObjectAnimator circle1Y = ObjectAnimator.ofFloat(circle1, "translationY", 0f, -40f, 30f, 0f);
        circle1Y.setDuration(5000);
        circle1Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle1Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle1Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1Y.start();

        // Circle 2 animation
        ObjectAnimator circle2X = ObjectAnimator.ofFloat(circle2, "translationX", 0f, -40f, 40f, 0f);
        circle2X.setDuration(4500);
        circle2X.setRepeatCount(ObjectAnimator.INFINITE);
        circle2X.setRepeatMode(ObjectAnimator.REVERSE);
        circle2X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2X.start();

        ObjectAnimator circle2Y = ObjectAnimator.ofFloat(circle2, "translationY", 0f, 30f, -20f, 0f);
        circle2Y.setDuration(4800);
        circle2Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle2Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle2Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2Y.start();
    }

    private void setupListeners() {
        btnSaveProfile.setOnClickListener(v -> {
            // Button click animation
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    })
                    .start();

            saveProfile();
        });

        btnEditPhoto.setOnClickListener(v -> {
            Toast.makeText(this, "Photo upload coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Focus change listeners to clear errors
        etAge.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) ageLayout.setError(null);
        });

        etHeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) heightLayout.setError(null);
        });

        etWeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) weightLayout.setError(null);
        });
    }

    private void setupBMIListener() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateBMI();
            }
        };

        etHeight.addTextChangedListener(watcher);
        etWeight.addTextChangedListener(watcher);
    }

    private void calculateBMI() {
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

            String category;
            int color;

            if (bmi < 18.5) {
                category = "Underweight";
                color = 0xFF2196F3; // Blue
            } else if (bmi < 25.0) {
                category = "Normal";
                color = 0xFF4CAF50; // Green
            } else if (bmi < 30.0) {
                category = "Overweight";
                color = 0xFFFF9800; // Orange
            } else {
                category = "Obese";
                color = 0xFFF44336; // Red
            }

            tvBMICategory.setText(category);
            tvBMICategory.setTextColor(color);
            tvBMIValue.setTextColor(color);

        } catch (NumberFormatException ignored) {}
    }

    private void loadExistingProfile() {
        try {
            String height = sessionManager.getUserHeight();
            String weight = sessionManager.getUserWeight();
            String age = sessionManager.getUserAge();
            String gender = sessionManager.getUserGender();
            String goal = sessionManager.getUserGoal();
            String level = sessionManager.getUserActivityLevel();

            if (!height.isEmpty()) etHeight.setText(height);
            if (!weight.isEmpty()) etWeight.setText(weight);
            if (!age.isEmpty()) etAge.setText(age);

            // Set gender
            if (gender.equals("Male")) {
                toggleGroup.check(R.id.btnMale);
            } else if (gender.equals("Female")) {
                toggleGroup.check(R.id.btnFemale);
            } else if (gender.equals("Other")) {
                toggleGroup.check(R.id.btnOther);
            }

            // Set goal spinner
            for (int i = 0; i < spinnerProfileGoal.getCount(); i++) {
                if (spinnerProfileGoal.getItemAtPosition(i).toString().equals(goal)) {
                    spinnerProfileGoal.setSelection(i);
                    break;
                }
            }

            // Set activity level spinner
            for (int i = 0; i < spinnerActivityLevel.getCount(); i++) {
                if (spinnerActivityLevel.getItemAtPosition(i).toString().equals(level)) {
                    spinnerActivityLevel.setSelection(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProfile() {
        // Clear previous errors
        ageLayout.setError(null);
        heightLayout.setError(null);
        weightLayout.setError(null);

        // Get values
        String age = etAge.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();

        // Validate age
        if (TextUtils.isEmpty(age)) {
            ageLayout.setError("Age is required");
            etAge.requestFocus();
            return;
        }

        try {
            int ageInt = Integer.parseInt(age);
            if (ageInt < 10 || ageInt > 100) {
                ageLayout.setError("Enter valid age (10–100)");
                etAge.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            ageLayout.setError("Invalid age");
            etAge.requestFocus();
            return;
        }

        // Validate height
        if (TextUtils.isEmpty(height)) {
            heightLayout.setError("Height is required");
            etHeight.requestFocus();
            return;
        }

        // Validate weight
        if (TextUtils.isEmpty(weight)) {
            weightLayout.setError("Weight is required");
            etWeight.requestFocus();
            return;
        }

        // Validate goal spinner
        if (spinnerProfileGoal.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a fitness goal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate activity level spinner
        if (spinnerActivityLevel.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select your activity level", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get gender
        int checkedId = toggleGroup.getCheckedButtonId();
        String gender; // Default
        if (checkedId == R.id.btnMale) gender = "Male";
        else if (checkedId == R.id.btnFemale) gender = "Female";
        else if (checkedId == R.id.btnOther) gender = "Other";
        else {
            gender = "Male";
        }

        // Calculate BMI
        double heightM = Double.parseDouble(height) / 100.0;
        double bmi = Double.parseDouble(weight) / (heightM * heightM);

        // Save profile
        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("SAVING...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // Save previous month data if first time
                if (!sessionManager.isProfileComplete()) {
                    sessionManager.savePrevMonthData(Float.parseFloat(weight), (float) bmi);
                }

                sessionManager.saveProfile(age, height, weight, gender,
                        spinnerProfileGoal.getSelectedItem().toString(),
                        spinnerActivityLevel.getSelectedItem().toString(),
                        (float) bmi);

                Toast.makeText(this, "Profile saved! Let's go! 🚀", Toast.LENGTH_SHORT).show();

                // Animate exit
                photoCard.animate()
                        .alpha(0f)
                        .translationY(-50f)
                        .setDuration(200)
                        .start();

                personalCard.animate()
                        .alpha(0f)
                        .translationY(-50f)
                        .setDuration(200)
                        .start();

                bodyCard.animate()
                        .alpha(0f)
                        .translationY(-50f)
                        .setDuration(200)
                        .start();

                fitnessCard.animate()
                        .alpha(0f)
                        .translationY(-50f)
                        .setDuration(200)
                        .start();

                btnSaveProfile.animate()
                        .alpha(0f)
                        .translationY(-50f)
                        .setDuration(200)
                        .withEndAction(() -> {
                            Intent intent = new Intent(ProfileSetupActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finishAffinity();
                        })
                        .start();

            } catch (Exception e) {
                e.printStackTrace();
                btnSaveProfile.setEnabled(true);
                btnSaveProfile.setText("SAVE & CONTINUE");
                Toast.makeText(this, "Error saving profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this, R.style.CustomAlertDialog)
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
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finishAffinity();
                })
                .setNegativeButton("No", null)
                .show();
    }

//    @Override
//    public void onBackPressed() {
//        showExitDialog();
//    }
}