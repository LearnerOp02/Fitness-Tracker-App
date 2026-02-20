package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone;
    private Spinner  spinnerGoal;
    private CheckBox cbTerms;
    private Button   btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initViews();
        setupGoalSpinner();
        setupListeners();
    }

    private void initViews() {
        etFullName        = findViewById(R.id.etFullName);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone           = findViewById(R.id.etPhone);
        spinnerGoal       = findViewById(R.id.spinnerGoal);
        cbTerms           = findViewById(R.id.cbTerms);
        btnRegister       = findViewById(R.id.btnRegister);
        tvLogin           = findViewById(R.id.tvLogin);
    }

    private void setupGoalSpinner() {
        String[] goals = {
                "Select Fitness Goal",
                "Lose Weight",
                "Build Muscle",
                "Improve Endurance",
                "Stay Active & Healthy",
                "Increase Flexibility",
                "Sports Performance"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, goals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(adapter);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());

        // Go back to Login
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name            = etFullName.getText().toString().trim();
        String email           = etEmail.getText().toString().trim();
        String password        = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone           = etPhone.getText().toString().trim();

        // ── Validation ──
        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus(); return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus(); return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus(); return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus(); return;
        }
        if (password.length() < 6) {
            etPassword.setError("Minimum 6 characters required");
            etPassword.requestFocus(); return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus(); return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus(); return;
        }
        if (phone.length() < 10) {
            etPhone.setError("Enter a valid 10-digit phone number");
            etPhone.requestFocus(); return;
        }
        if (spinnerGoal.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a fitness goal", Toast.LENGTH_SHORT).show(); return;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms & Privacy Policy", Toast.LENGTH_SHORT).show(); return;
        }

        // Show loading state
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        new Handler().postDelayed(() -> registerUser(name, email, password, phone,
                spinnerGoal.getSelectedItem().toString()), 1200);
    }

    private void registerUser(String name, String email, String password,
                              String phone, String goal) {
        // Save user credentials and details to SharedPreferences
        getSharedPreferences("FitLifePrefs", MODE_PRIVATE)
                .edit()
                .putString("userName",    name)
                .putString("userEmail",   email)
                .putString("userPassword", password)   // saved for login check
                .putString("userPhone",   phone)
                .putString("userGoal",    goal)
                .putBoolean("isLoggedIn", true)
                .putBoolean("profileComplete", false)  // profile setup still needed
                .apply();

        Toast.makeText(this,
                "Account created! Let's set up your profile.", Toast.LENGTH_LONG).show();

        // Go to Profile Setup (not Home — profile is not done yet)
        startActivity(new Intent(RegisterActivity.this, ProfileSetupActivity.class));
        finishAffinity();
    }
}