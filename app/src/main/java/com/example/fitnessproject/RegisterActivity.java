package com.example.fitnessproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, emailLayout, passwordLayout, confirmPasswordLayout, phoneLayout;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone;
    private Spinner spinnerGoal;
    private MaterialCheckBox cbTerms;
    private Button btnRegister;
    private TextView tvLogin;
    private MaterialCardView registerCard;
    private LinearLayout loginRedirect;
    private View circle1, circle2;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initSessionManager();
        initViews();
        setupGoalSpinner();
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
        // Background circles
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);

        // Card and redirect
        registerCard = findViewById(R.id.registerCard);
        loginRedirect = findViewById(R.id.loginRedirect);

        // TextInputLayouts
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        phoneLayout = findViewById(R.id.phoneLayout);

        // EditTexts
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);

        // Spinner
        spinnerGoal = findViewById(R.id.spinnerGoal);

        // Checkbox
        cbTerms = findViewById(R.id.cbTerms);

        // Button
        btnRegister = findViewById(R.id.btnRegister);

        // TextView
        tvLogin = findViewById(R.id.tvLogin);
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

    private void startAnimations() {
        // Animate background circles
        animateBackgroundCircles();

        // Animate card entrance
        registerCard.setVisibility(View.VISIBLE);
        registerCard.setAlpha(0f);
        registerCard.setTranslationY(100f);
        registerCard.setScaleX(0.95f);
        registerCard.setScaleY(0.95f);

        ObjectAnimator cardAlpha = ObjectAnimator.ofFloat(registerCard, "alpha", 0f, 1f);
        ObjectAnimator cardTranslation = ObjectAnimator.ofFloat(registerCard, "translationY", 100f, 0f);
        ObjectAnimator cardScaleX = ObjectAnimator.ofFloat(registerCard, "scaleX", 0.95f, 1f);
        ObjectAnimator cardScaleY = ObjectAnimator.ofFloat(registerCard, "scaleY", 0.95f, 1f);

        AnimatorSet cardAnim = new AnimatorSet();
        cardAnim.playTogether(cardAlpha, cardTranslation, cardScaleX, cardScaleY);
        cardAnim.setDuration(800);
        cardAnim.setInterpolator(new AnticipateOvershootInterpolator(1.2f));
        cardAnim.start();

        // Animate login redirect with delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loginRedirect.setVisibility(View.VISIBLE);
            loginRedirect.setAlpha(0f);
            loginRedirect.setTranslationY(20f);

            ObjectAnimator redirectAlpha = ObjectAnimator.ofFloat(loginRedirect, "alpha", 0f, 1f);
            ObjectAnimator redirectTranslation = ObjectAnimator.ofFloat(loginRedirect, "translationY", 20f, 0f);

            AnimatorSet redirectAnim = new AnimatorSet();
            redirectAnim.playTogether(redirectAlpha, redirectTranslation);
            redirectAnim.setDuration(600);
            redirectAnim.setInterpolator(new DecelerateInterpolator());
            redirectAnim.start();
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

        ObjectAnimator circle2Y = ObjectAnimator.ofFloat(circle2, "translationY", 0f, 30f, -20f, 0f);
        circle2Y.setDuration(4800);
        circle2Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle2Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle2Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2Y.start();
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            // Button click animation
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    })
                    .start();

            attemptRegister();
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Focus change listeners to clear errors
        etFullName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) nameLayout.setError(null);
        });

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) emailLayout.setError(null);
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) passwordLayout.setError(null);
        });

        etConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) confirmPasswordLayout.setError(null);
        });

        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) phoneLayout.setError(null);
        });
    }

    private void attemptRegister() {
        // Clear previous errors
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        phoneLayout.setError(null);

        // Get input values
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Name is required");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            phoneLayout.setError("Enter a valid phone number (min 10 digits)");
            etPhone.requestFocus();
            return;
        }

        if (spinnerGoal.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a fitness goal", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            // Shake animation for checkbox
            cbTerms.animate()
                    .translationX(-10f)
                    .setDuration(50)
                    .withEndAction(() ->
                            cbTerms.animate()
                                    .translationX(10f)
                                    .setDuration(50)
                                    .withEndAction(() ->
                                            cbTerms.animate()
                                                    .translationX(0f)
                                                    .setDuration(50)
                                                    .start())
                                    .start())
                    .start();
            return;
        }

        // Disable button and show loading
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        // Simulate network delay
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                        registerUser(name, email, password, phone, spinnerGoal.getSelectedItem().toString()),
                1500);
    }

    private void registerUser(String name, String email, String password, String phone, String goal) {
        try {
            // Create session
            sessionManager.createRegisterSession(name, email, password, phone, goal);

            Toast.makeText(this,
                    "Account created successfully! Let's login now.",
                    Toast.LENGTH_LONG).show();

            // Animate exit
            registerCard.animate()
                    .alpha(0f)
                    .translationY(-100f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finishAffinity();
                    })
                    .start();

        } catch (Exception e) {
            e.printStackTrace();
            btnRegister.setEnabled(true);
            btnRegister.setText("CREATE ACCOUNT");
            Toast.makeText(this,
                    "Registration failed. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//    }
}