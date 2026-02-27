package com.example.fitnessproject;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String STATIC_EMAIL = "admin@fitlife.com";
    private static final String STATIC_PASSWORD = "fitlife123";

    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin, btnGoogleLogin;
    private TextView tvForgotPassword, tvSignUp;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
        initViews();
        setupListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sessionManager.isLoggedIn()) {
            goToNext();
        }
    }

    private void initViews() {
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
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

        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

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
            etLoginPassword.setError("Password must be at least 6 characters");
            etLoginPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        new Handler().postDelayed(() -> loginUser(email, password), 1200);
    }

    private void loginUser(String email, String password) {
        String savedEmail = sessionManager.getUserEmail();
        String savedPassword = sessionManager.getUserPassword();

        boolean staticMatch = email.equals(STATIC_EMAIL) && password.equals(STATIC_PASSWORD);
        boolean registeredMatch = email.equals(savedEmail) && password.equals(savedPassword);

        if (staticMatch || registeredMatch) {
            if (staticMatch) {
                sessionManager.createLoginSession("Admin User", email, password);
            }

            Toast.makeText(this, "Welcome back! 💪", Toast.LENGTH_SHORT).show();
            goToNext();

        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText("LOG IN");
            etLoginPassword.setError("Invalid email or password");
            etLoginPassword.requestFocus();
            Toast.makeText(this,
                    "Invalid credentials.\nHint: admin@fitlife.com / fitlife123",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void goToNext() {
        boolean profileComplete = sessionManager.isProfileComplete();
        Intent intent;

        if (profileComplete) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, ProfileSetupActivity.class);
        }
        startActivity(intent);
        finish();
    }
}