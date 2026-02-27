package com.example.fitnessproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class BmiCalculatorActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etBmiHeight, etBmiWeight;
    private TextView tvBmiValue, tvBmiCategory, tvBmiTip;
    private View bmiMarker;
    private Button btnSaveBmi;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmi_calculator);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
        initViews();
        prefillFromProfile();
        setupLiveCalculation();
        btnBack.setOnClickListener(v -> finish());
        btnSaveBmi.setOnClickListener(v -> saveBmiToProfile());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etBmiHeight = findViewById(R.id.etBmiHeight);
        etBmiWeight = findViewById(R.id.etBmiWeight);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvBmiCategory = findViewById(R.id.tvBmiCategory);
        tvBmiTip = findViewById(R.id.tvBmiTip);
        bmiMarker = findViewById(R.id.bmiMarker);
        btnSaveBmi = findViewById(R.id.btnSaveBmi);
    }

    private void prefillFromProfile() {
        String h = sessionManager.getUserHeight();
        String w = sessionManager.getUserWeight();
        if (!h.isEmpty()) etBmiHeight.setText(h);
        if (!w.isEmpty()) etBmiWeight.setText(w);
    }

    private void setupLiveCalculation() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int c, int a) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int b, int c) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateBMI();
            }
        };
        etBmiHeight.addTextChangedListener(watcher);
        etBmiWeight.addTextChangedListener(watcher);
    }

    private void calculateBMI() {
        String hStr = etBmiHeight.getText().toString().trim();
        String wStr = etBmiWeight.getText().toString().trim();
        if (hStr.isEmpty() || wStr.isEmpty()) return;

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
                color = 0xFF2196F3;
                tip = "💡 Increase caloric intake and strength training.";
                markerPct = bmi / 18.5f * 0.25f;
            } else if (bmi < 25f) {
                category = "Normal Weight ✓";
                color = 0xFF4CAF50;
                tip = "✅ Great! Maintain your current routine.";
                markerPct = 0.25f + ((bmi - 18.5f) / 6.5f) * 0.25f;
            } else if (bmi < 30f) {
                category = "Overweight";
                color = 0xFFFF9800;
                tip = "⚠️ Add cardio sessions and reduce calorie intake.";
                markerPct = 0.50f + ((bmi - 25f) / 5f) * 0.25f;
            } else {
                category = "Obese";
                color = 0xFFF44336;
                tip = "🔴 Consult a physician and start low-intensity workouts.";
                markerPct = Math.min(0.75f + ((bmi - 30f) / 10f) * 0.25f, 0.97f);
            }

            tvBmiCategory.setText(category);
            tvBmiCategory.setTextColor(color);
            tvBmiValue.setTextColor(color);
            tvBmiTip.setText(tip);

            bmiMarker.post(() -> {
                ViewGroup parent = (ViewGroup) bmiMarker.getParent();
                int barWidth = parent.getWidth();
                bmiMarker.setTranslationX(barWidth * markerPct);
            });

        } catch (NumberFormatException ignored) {
        }
    }

    private void saveBmiToProfile() {
        String hStr = etBmiHeight.getText().toString().trim();
        String wStr = etBmiWeight.getText().toString().trim();
        if (hStr.isEmpty() || wStr.isEmpty()) {
            Toast.makeText(this, "Please enter height and weight", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float h = Float.parseFloat(hStr);
            float w = Float.parseFloat(wStr);
            float bmi = w / ((h / 100f) * (h / 100f));

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

            Toast.makeText(this, "BMI saved to profile ✓", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Invalid values", Toast.LENGTH_SHORT).show();
        }
    }
}