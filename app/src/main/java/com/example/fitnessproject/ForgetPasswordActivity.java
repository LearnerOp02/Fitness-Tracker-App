package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText    etForgotEmail;
    private Button      btnSendOTP;
    private TextView    tvBackToLogin;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        initViews();
        setupListeners();
    }

    private void initViews() {
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendOTP    = findViewById(R.id.btnSendOTP);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        btnBack       = findViewById(R.id.btnBack);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnSendOTP.setOnClickListener(v -> attemptSendOTP());
    }

    private void attemptSendOTP() {
        String email = etForgotEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etForgotEmail.setError("Please enter your email address");
            etForgotEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etForgotEmail.setError("Enter a valid email address");
            etForgotEmail.requestFocus();
            return;
        }

        btnSendOTP.setEnabled(false);
        btnSendOTP.setText("Sending OTP...");

        new Handler().postDelayed(() -> {
            btnSendOTP.setEnabled(true);
            btnSendOTP.setText("SEND OTP");

            Toast.makeText(this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();

            // Pass email to OTP screen
            Intent intent = new Intent(ForgetPasswordActivity.this, OtpVerifyActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }, 1200);
    }
}