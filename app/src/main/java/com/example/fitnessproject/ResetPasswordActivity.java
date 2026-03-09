package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView resetCard;
    private TextInputLayout newPasswordLayout, confirmPasswordLayout;
    private EditText etNewPassword, etConfirmNewPassword;
    private Button btnResetPassword;
    private TextView tvPasswordStrength, tvStepIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        initViews();
        setupToolbar();
        setupListeners();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
        resetCard = findViewById(R.id.resetCard);
//        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        tvStepIndicator = findViewById(R.id.tvStepIndicator);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Reset Password", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(resetCard, 0);
    }

    private void setupListeners() {
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updatePasswordStrength(s.toString());
            }
        });

        btnResetPassword.setOnClickListener(v -> {
            animateClick(v);
            attemptReset();
        });
    }

    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            tvPasswordStrength.setText("");
            return;
        }

        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=].*")) strength++;

        String label;
        int color;
        if (strength <= 1) {
            label = "Weak password";
            color = getResources().getColor(R.color.obese);
        } else if (strength <= 3) {
            label = "Moderate password";
            color = getResources().getColor(R.color.overweight);
        } else {
            label = "Strong password ✓";
            color = getResources().getColor(R.color.normal);
        }

        tvPasswordStrength.setText(label);
        tvPasswordStrength.setTextColor(color);
    }

    private void attemptReset() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordLayout.setError("Enter a new password");
            etNewPassword.requestFocus();
            return;
        }
        if (newPassword.length() < 6) {
            newPasswordLayout.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            etConfirmNewPassword.requestFocus();
            return;
        }

        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Resetting...");

        handler.postDelayed(() -> {
            // Get email from intent
            String userEmail = getIntent().getStringExtra("email");

            // Update saved password in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
            String savedEmail = prefs.getString("userEmail", "");

            // Update password only for registered user
            if (userEmail != null && userEmail.equals(savedEmail)) {
                prefs.edit().putString("userPassword", newPassword).apply();
            }

            // Navigate to success screen
            startActivity(new Intent(ResetPasswordActivity.this, ResetSuccessActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        }, 1200);
    }
}