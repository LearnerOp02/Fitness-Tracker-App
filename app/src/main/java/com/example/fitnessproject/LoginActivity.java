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

        initViews();
        setupListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                goToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (btnLogin != null) btnLogin.setOnClickListener(v -> attemptLogin());

        if (btnGoogleLogin != null) {
            btnGoogleLogin.setOnClickListener(v ->
                    Toast.makeText(this, "Google Sign-In coming soon!", Toast.LENGTH_SHORT).show());
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            });
        }

        if (tvSignUp != null) {
            tvSignUp.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }

    private boolean isNetworkConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void attemptLogin() {
        if (etLoginEmail == null || etLoginPassword == null || btnLogin == null) return;

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
        try {
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
                if (btnLogin != null) {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOG IN");
                }
                if (etLoginPassword != null) {
                    etLoginPassword.setError("Invalid email or password");
                    etLoginPassword.requestFocus();
                }
                Toast.makeText(this,
                        "Invalid credentials.\nHint: admin@fitlife.com / fitlife123",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (btnLogin != null) {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOG IN");
            }
            Toast.makeText(this, "Login error. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToNext() {
        try {
            if (sessionManager == null) {
                sessionManager = new UserSessionManager(this);
            }
            boolean profileComplete = sessionManager.isProfileComplete();
            Intent intent;

            if (profileComplete) {
                intent = new Intent(LoginActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Error navigating. Please restart app.", Toast.LENGTH_LONG).show();
            }
        }
    }
}