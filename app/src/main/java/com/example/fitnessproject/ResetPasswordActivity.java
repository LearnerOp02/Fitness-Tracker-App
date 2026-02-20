package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText    etNewPassword, etConfirmNewPassword;
    private ImageButton btnToggleNewPassword, btnToggleConfirmPassword, btnBack;
    private Button      btnResetPassword;
    private TextView    tvPasswordStrength;

    private boolean newPasswordVisible     = false;
    private boolean confirmPasswordVisible = false;
    private String  userEmail              = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) userEmail = "";

        initViews();
        setupListeners();
    }

    private void initViews() {
        etNewPassword          = findViewById(R.id.etNewPassword);
        etConfirmNewPassword   = findViewById(R.id.etConfirmNewPassword);
        btnToggleNewPassword   = findViewById(R.id.btnToggleNewPassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        btnResetPassword       = findViewById(R.id.btnResetPassword);
        tvPasswordStrength     = findViewById(R.id.tvPasswordStrength);
        btnBack                = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Toggle show/hide new password
        btnToggleNewPassword.setOnClickListener(v -> {
            newPasswordVisible = !newPasswordVisible;
            etNewPassword.setTransformationMethod(newPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnToggleNewPassword.setImageResource(newPasswordVisible
                    ? R.drawable.ic_eye_on : R.drawable.ic_eye_off);
            etNewPassword.setSelection(etNewPassword.getText().length());
        });

        // Toggle show/hide confirm password
        btnToggleConfirmPassword.setOnClickListener(v -> {
            confirmPasswordVisible = !confirmPasswordVisible;
            etConfirmNewPassword.setTransformationMethod(confirmPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnToggleConfirmPassword.setImageResource(confirmPasswordVisible
                    ? R.drawable.ic_eye_on : R.drawable.ic_eye_off);
            etConfirmNewPassword.setSelection(etConfirmNewPassword.getText().length());
        });

        // Live password strength indicator
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                updatePasswordStrength(s.toString());
            }
        });

        btnResetPassword.setOnClickListener(v -> attemptReset());
    }

    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            tvPasswordStrength.setText("");
            return;
        }

        int strength = 0;
        if (password.length() >= 8)                          strength++;
        if (password.matches(".*[A-Z].*"))                   strength++;
        if (password.matches(".*[a-z].*"))                   strength++;
        if (password.matches(".*[0-9].*"))                   strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=].*"))     strength++;

        String label; int color;
        if      (strength <= 1) { label = "Weak password";      color = 0xFFF44336; }
        else if (strength <= 3) { label = "Moderate password";   color = 0xFFFF9800; }
        else                    { label = "Strong password ✓";   color = 0xFF4CAF50; }

        tvPasswordStrength.setText(label);
        tvPasswordStrength.setTextColor(color);
    }

    private void attemptReset() {
        String newPassword     = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Enter a new password");
            etNewPassword.requestFocus(); return;
        }
        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus(); return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmNewPassword.setError("Passwords do not match");
            etConfirmNewPassword.requestFocus(); return;
        }

        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Resetting...");

        new Handler().postDelayed(() -> {
            // Update saved password in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
            String savedEmail = prefs.getString("userEmail", "");

            // Update password only for registered user — static admin is always fixed
            if (userEmail.equals(savedEmail)) {
                prefs.edit().putString("userPassword", newPassword).apply();
            }

            // Navigate to success screen
            startActivity(new Intent(ResetPasswordActivity.this, ResetSuccessActivity.class));
            finishAffinity();
        }, 1200);
    }
}