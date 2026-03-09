package com.example.fitnessproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

public class BmiCalculatorActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView bmiCard, inputCard;
    private TextInputLayout heightLayout, weightLayout;
    private EditText etHeight, etWeight;
    private TextView tvBmiValue, tvBmiCategory, tvBmiTip;
    private View bmiMarker;
    private Button btnSaveBmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmi_calculator);

        initViews();
        setupToolbar();
        prefillFromProfile();
        setupLiveCalculation();
        setupListeners();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
        bmiCard = findViewById(R.id.bmiCard);
        inputCard = findViewById(R.id.inputCard);
        heightLayout = findViewById(R.id.heightLayout);
        weightLayout = findViewById(R.id.weightLayout);
        etHeight = findViewById(R.id.etBmiHeight);
        etWeight = findViewById(R.id.etBmiWeight);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvBmiCategory = findViewById(R.id.tvBmiCategory);
        tvBmiTip = findViewById(R.id.tvBmiTip);
        bmiMarker = findViewById(R.id.bmiMarker);
        btnSaveBmi = findViewById(R.id.btnSaveBmi);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "BMI Calculator", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(bmiCard, 0);
        animateCard(inputCard, 150);
    }

    private void prefillFromProfile() {
        String h = sessionManager.getUserHeight();
        String w = sessionManager.getUserWeight();
        if (!h.isEmpty()) etHeight.setText(h);
        if (!w.isEmpty()) etWeight.setText(w);
    }

    private void setupLiveCalculation() {
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
        String hStr = etHeight.getText().toString().trim();
        String wStr = etWeight.getText().toString().trim();

        if (hStr.isEmpty() || wStr.isEmpty()) {
            tvBmiValue.setText("--");
            tvBmiCategory.setText("Enter your details");
            tvBmiTip.setText("");
            return;
        }

        try {
            float heightCm = Float.parseFloat(hStr);
            float weightKg = Float.parseFloat(wStr);

            if (heightCm <= 0 || weightKg <= 0) return;

            float heightM = heightCm / 100f;
            float bmi = weightKg / (heightM * heightM);

            tvBmiValue.setText(String.format("%.1f", bmi));

            String category, tip;
            int color;
            float markerPct;

            if (bmi < 18.5f) {
                category = "Underweight";
                color = getResources().getColor(R.color.underweight);
                tip = "💡 Increase caloric intake and strength training.";
                markerPct = bmi / 18.5f * 0.25f;
            } else if (bmi < 25f) {
                category = "Normal Weight";
                color = getResources().getColor(R.color.normal);
                tip = "✅ Great! Maintain your current routine.";
                markerPct = 0.25f + ((bmi - 18.5f) / 6.5f) * 0.25f;
            } else if (bmi < 30f) {
                category = "Overweight";
                color = getResources().getColor(R.color.overweight);
                tip = "⚠️ Add cardio sessions and reduce calorie intake.";
                markerPct = 0.50f + ((bmi - 25f) / 5f) * 0.25f;
            } else {
                category = "Obese";
                color = getResources().getColor(R.color.obese);
                tip = "🔴 Consult a physician and start low-intensity workouts.";
                markerPct = Math.min(0.75f + ((bmi - 30f) / 10f) * 0.25f, 0.97f);
            }

            tvBmiCategory.setText(category);
            tvBmiCategory.setTextColor(color);
            tvBmiValue.setTextColor(color);
            tvBmiTip.setText(tip);

            // Update marker position
            bmiMarker.post(() -> {
                ViewGroup parent = (ViewGroup) bmiMarker.getParent();
                int barWidth = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
                bmiMarker.setTranslationX(barWidth * markerPct);
            });

        } catch (NumberFormatException ignored) {}
    }

    private void setupListeners() {
        btnSaveBmi.setOnClickListener(v -> {
            animateClick(v);
            saveBmiToProfile();
        });
    }

    private void saveBmiToProfile() {
        String hStr = etHeight.getText().toString().trim();
        String wStr = etWeight.getText().toString().trim();

        if (hStr.isEmpty() || wStr.isEmpty()) {
            showToast("Please enter height and weight");
            return;
        }

        try {
            float h = Float.parseFloat(hStr);
            float w = Float.parseFloat(wStr);
            float bmi = w / ((h / 100f) * (h / 100f));

            btnSaveBmi.setEnabled(false);
            btnSaveBmi.setText("SAVING...");

            handler.postDelayed(() -> {
                try {
                    // Save current as prev month before updating
                    float currentWeight = 0;
                    float currentBmi = 0;
                    try {
                        currentWeight = Float.parseFloat(sessionManager.getUserWeight());
                        currentBmi = sessionManager.getBMI();
                        sessionManager.savePrevMonthData(currentWeight, currentBmi);
                    } catch (Exception e) {
                        // First time saving
                    }

                    // Update profile
                    sessionManager.saveProfile(
                            sessionManager.getUserAge(),
                            hStr,
                            wStr,
                            sessionManager.getUserGender(),
                            sessionManager.getUserGoal(),
                            sessionManager.getUserActivityLevel(),
                            bmi
                    );

                    showToast("BMI saved to profile ✓");

                    // Animate exit
                    bmiCard.animate()
                            .alpha(0f)
                            .translationY(-50f)
                            .setDuration(200)
                            .start();

                    inputCard.animate()
                            .alpha(0f)
                            .translationY(-50f)
                            .setDuration(200)
                            .withEndAction(() -> finish())
                            .start();

                } catch (Exception e) {
                    e.printStackTrace();
                    btnSaveBmi.setEnabled(true);
                    btnSaveBmi.setText("SAVE TO PROFILE");
                    showToast("Error saving BMI");
                }
            }, 1500);

        } catch (Exception e) {
            showToast("Invalid values");
        }
    }
}