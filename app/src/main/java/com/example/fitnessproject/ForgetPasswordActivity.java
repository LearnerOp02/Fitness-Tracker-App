package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

public class ForgetPasswordActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView forgotCard;
    private TextInputLayout emailLayout;
    private EditText etForgotEmail;
    private Button btnSendOTP;
    private TextView tvBackToLogin, tvStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        initViews();
        setupToolbar();
        startAnimations();
        setupListeners();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
        forgotCard = findViewById(R.id.forgotCard);
        emailLayout = findViewById(R.id.emailLayout);
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        tvStep = findViewById(R.id.tvStep);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Forgot Password", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(forgotCard, 0);
    }

    private void setupListeners() {
        btnSendOTP.setOnClickListener(v -> {
            animateClick(v);
            attemptSendOTP();
        });

        tvBackToLogin.setOnClickListener(v -> {
            navigateTo(LoginActivity.class);
            finish();
        });
    }

    private void attemptSendOTP() {
        String email = etForgotEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Please enter your email address");
            etForgotEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter a valid email address");
            etForgotEmail.requestFocus();
            return;
        }

        btnSendOTP.setEnabled(false);
        btnSendOTP.setText("Sending OTP...");

        handler.postDelayed(() -> {
            btnSendOTP.setEnabled(true);
            btnSendOTP.setText("SEND OTP");

            showToast("OTP sent to " + email);

            Intent intent = new Intent(ForgetPasswordActivity.this, OtpVerifyActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 1200);
    }
}