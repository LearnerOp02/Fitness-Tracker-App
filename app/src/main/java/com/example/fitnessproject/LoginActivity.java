package com.example.fitnessproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private static final String STATIC_EMAIL = "admin@fitlife.com";
    private static final String STATIC_PASSWORD = "fitlife123";

    private TextInputLayout emailLayout, passwordLayout;
    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin, btnGoogleLogin;
    private TextView tvForgotPassword, tvSignUp;
    private MaterialCardView loginCard;
    private LinearLayout signupRedirect;
    private View circle1, circle2;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initSessionManager();
        initViews();
        startAnimations();
        setupListeners();
    }

    private void initSessionManager() {
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
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        loginCard = findViewById(R.id.loginCard);
        signupRedirect = findViewById(R.id.signupRedirect);

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void startAnimations() {
        // Animate background circles
        animateBackgroundCircles();

        // Animate card entrance
        loginCard.setVisibility(View.VISIBLE);
        loginCard.setAlpha(0f);
        loginCard.setTranslationY(100f);

        ObjectAnimator cardAlpha = ObjectAnimator.ofFloat(loginCard, "alpha", 0f, 1f);
        ObjectAnimator cardTranslation = ObjectAnimator.ofFloat(loginCard, "translationY", 100f, 0f);

        AnimatorSet cardAnim = new AnimatorSet();
        cardAnim.playTogether(cardAlpha, cardTranslation);
        cardAnim.setDuration(800);
        cardAnim.setInterpolator(new AnticipateOvershootInterpolator(1.2f));
        cardAnim.start();

        // Animate signup redirect
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            signupRedirect.setVisibility(View.VISIBLE);
            signupRedirect.setAlpha(0f);

            ObjectAnimator redirectAlpha = ObjectAnimator.ofFloat(signupRedirect, "alpha", 0f, 1f);
            redirectAlpha.setDuration(600);
            redirectAlpha.setInterpolator(new DecelerateInterpolator());
            redirectAlpha.start();
        }, 500);
    }

    private void animateBackgroundCircles() {
        // Circle 1 animation
        ObjectAnimator circle1X = ObjectAnimator.ofFloat(circle1, "translationX", 0f, 50f, -30f, 0f);
        circle1X.setDuration(4000);
        circle1X.setRepeatCount(ObjectAnimator.INFINITE);
        circle1X.setRepeatMode(ObjectAnimator.REVERSE);
        circle1X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1X.start();

        ObjectAnimator circle1Y = ObjectAnimator.ofFloat(circle1, "translationY", 0f, -40f, 30f, 0f);
        circle1Y.setDuration(5000);
        circle1Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle1Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle1Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1Y.start();

        // Circle 2 animation
        ObjectAnimator circle2X = ObjectAnimator.ofFloat(circle2, "translationX", 0f, -40f, 40f, 0f);
        circle2X.setDuration(4500);
        circle2X.setRepeatCount(ObjectAnimator.INFINITE);
        circle2X.setRepeatMode(ObjectAnimator.REVERSE);
        circle2X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2X.start();
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        btnGoogleLogin.setOnClickListener(v ->
                Toast.makeText(this, "Google Sign-In coming soon!", Toast.LENGTH_SHORT).show());

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
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
        if (!isNetworkConnected()) {
            Toast.makeText(this, "No internet connection. Please check your network.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            etLoginEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email address");
            etLoginEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            etLoginPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            etLoginPassword.requestFocus();
            return;
        }

        // Clear errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Animate button
        btnLogin.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    btnLogin.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();
                })
                .start();

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> loginUser(email, password), 1200);
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

                // Animate exit
                loginCard.animate()
                        .alpha(0f)
                        .translationY(-100f)
                        .setDuration(300)
                        .withEndAction(this::goToNext)
                        .start();
            } else {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOG IN");
                passwordLayout.setError("Invalid email or password");
                etLoginPassword.requestFocus();

                Toast.makeText(this,
                        "Invalid credentials.\nHint: admin@fitlife.com / fitlife123",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            btnLogin.setEnabled(true);
            btnLogin.setText("LOG IN");
            Toast.makeText(this, "Login error. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToNext() {
        try {
            boolean profileComplete = sessionManager.isProfileComplete();
            Intent intent;

            if (profileComplete) {
                intent = new Intent(LoginActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}