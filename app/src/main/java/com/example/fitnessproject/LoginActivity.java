package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String STATIC_EMAIL    = "admin@fitlife.com";
    private static final String STATIC_PASSWORD = "fitlife123";

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

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class)));

        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void attemptLogin() {
        if (!isNetworkConnected()) {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
            return;
        }

        String email    = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError(getString(R.string.error_field_required));
            etLoginEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError(getString(R.string.error_valid_email));
            etLoginEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError(getString(R.string.error_field_required));
            etLoginPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etLoginPassword.setError(getString(R.string.error_password_min));
            etLoginPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        new Handler().postDelayed(() -> loginUser(email, password), 1200);
    }

    private void loginUser(String email, String password) {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);

        String savedEmail    = prefs.getString("userEmail", "");
        String savedPassword = prefs.getString("userPassword", "");

        boolean staticMatch    = email.equals(STATIC_EMAIL) && password.equals(STATIC_PASSWORD);
        boolean registeredMatch = email.equals(savedEmail) && password.equals(savedPassword);

        if (staticMatch || registeredMatch) {
            prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("userName", staticMatch ? "Admin User" : prefs.getString("userName", "Athlete"))
                    .putString("userEmail", email)
                    .apply();

            Toast.makeText(this, "Welcome back! 💪", Toast.LENGTH_SHORT).show();
            goToNext(prefs);

        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText(R.string.login);
            etLoginPassword.setError("Invalid email or password");
            etLoginPassword.requestFocus();
            Toast.makeText(this,
                    "Invalid credentials.\nHint: admin@fitlife.com / fitlife123",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void goToNext(SharedPreferences prefs) {
        boolean profileComplete = prefs.getBoolean("profileComplete", false);
        Intent intent;

        if (profileComplete) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            String userName = prefs.getString("userName", "");
            if (userName.isEmpty()) {
                intent = new Intent(this, ProfileSetupActivity.class);
            } else {
                intent = new Intent(this, ProfileSetupActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }
}