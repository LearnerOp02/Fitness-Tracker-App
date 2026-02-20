package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // ─── STATIC CREDENTIALS (for demo / viva) ────────────────────────────────
    private static final String STATIC_EMAIL    = "admin@fitlife.com";
    private static final String STATIC_PASSWORD = "fitlife123";
    // ─────────────────────────────────────────────────────────────────────────

    private EditText etLoginEmail, etLoginPassword;
    private Button   btnLogin, btnGoogleLogin;
    private TextView tvForgotPassword, tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initViews();
        setupListeners();
    }

    // Skip login screen if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            goToNext(prefs);
        }
    }

    private void initViews() {
        etLoginEmail     = findViewById(R.id.etLoginEmail);
        etLoginPassword  = findViewById(R.id.etLoginPassword);
        btnLogin         = findViewById(R.id.btnLogin);
        btnGoogleLogin   = findViewById(R.id.btnGoogleLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp         = findViewById(R.id.tvSignUp);
    }

    private void setupListeners() {

        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGoogleLogin.setOnClickListener(v ->
                Toast.makeText(this, "Google Sign-In coming soon!", Toast.LENGTH_SHORT).show());

        // Navigate to Forgot Password screen
        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class)));

        // Navigate to Register screen
        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email    = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        // ── Field validation ──
        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError("Email is required");
            etLoginEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError("Enter a valid email address");
            etLoginEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError("Password is required");
            etLoginPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etLoginPassword.setError("Minimum 6 characters required");
            etLoginPassword.requestFocus();
            return;
        }

        // Show loading state
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        new Handler().postDelayed(() -> loginUser(email, password), 1200);
    }

    private void loginUser(String email, String password) {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);

        // ── Check static credentials OR previously registered user ──
        String savedEmail    = prefs.getString("userEmail", "");
        String savedPassword = prefs.getString("userPassword", "");

        boolean staticMatch    = email.equals(STATIC_EMAIL) && password.equals(STATIC_PASSWORD);
        boolean registeredMatch = email.equals(savedEmail) && password.equals(savedPassword);

        if (staticMatch || registeredMatch) {
            // Save login state
            prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("userName", staticMatch ? "Admin User" : prefs.getString("userName", "Athlete"))
                    .putString("userEmail", email)
                    .apply();

            Toast.makeText(this, "Welcome back! 💪", Toast.LENGTH_SHORT).show();
            goToNext(prefs);

        } else {
            // Failed — reset button
            btnLogin.setEnabled(true);
            btnLogin.setText("LOG IN");
            etLoginPassword.setError("Invalid email or password");
            etLoginPassword.requestFocus();
            Toast.makeText(this,
                    "Invalid credentials.\nHint: admin@fitlife.com / fitlife123",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Navigate to Profile Setup if not complete, else Home
    private void goToNext(SharedPreferences prefs) {
        boolean profileComplete = prefs.getBoolean("profileComplete", false);
        Intent intent = profileComplete
                ? new Intent(this, HomeActivity.class)
                : new Intent(this, ProfileSetupActivity.class);
        startActivity(intent);
        finish();
    }
}