package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
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

        if (TextUtils.isEmpty(name)) {
            etFullName.setError(getString(R.string.error_field_required));
            etFullName.requestFocus(); return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            etEmail.requestFocus(); return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_valid_email));
            etEmail.requestFocus(); return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            etPassword.requestFocus(); return;
        }
        if (password.length() < 6) {
            etPassword.setError(getString(R.string.error_password_min));
            etPassword.requestFocus(); return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_password_mismatch));
            etConfirmPassword.requestFocus(); return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError(getString(R.string.error_field_required));
            etPhone.requestFocus(); return;
        }
        if (phone.length() < 10) {
            etPhone.setError(getString(R.string.error_phone_valid));
            etPhone.requestFocus(); return;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus(); return;
        }
        if (spinnerGoal.getSelectedItemPosition() == 0) {
            Toast.makeText(this, getString(R.string.error_select_goal), Toast.LENGTH_SHORT).show(); return;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, getString(R.string.error_agree_terms), Toast.LENGTH_SHORT).show(); return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        new Handler().postDelayed(() -> registerUser(name, email, password, phone,
                spinnerGoal.getSelectedItem().toString()), 1200);
    }

    private void registerUser(String name, String email, String password,
                              String phone, String goal) {
        getSharedPreferences("FitLifePrefs", MODE_PRIVATE)
                .edit()
                .putString("userName",    name)
                .putString("userEmail",   email)
                .putString("userPassword", password)
                .putString("userPhone",   phone)
                .putString("userGoal",    goal)
                .putBoolean("isLoggedIn", true)
                .putBoolean("profileComplete", false)
                .apply();

        Toast.makeText(this,
                "Account created! Let's set up your profile.", Toast.LENGTH_LONG).show();

        startActivity(new Intent(RegisterActivity.this, ProfileSetupActivity.class));
        finishAffinity();
    }
}