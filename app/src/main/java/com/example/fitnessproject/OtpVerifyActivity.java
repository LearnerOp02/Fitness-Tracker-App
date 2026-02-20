package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OtpVerifyActivity extends AppCompatActivity {

    // ── Views (IDs match activity_otp_verify.xml exactly) ───────────────────
    private ImageButton btnBack;
    private TextView    tvOtpEmail;
    private TextView    tvCountdown;
    private TextView    tvResendOtp;
    private Button      btnVerifyOtp;
    private EditText    etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;

    // ── Class-level array built after all fields assigned ────────────────────
    private EditText[] otpBoxes;

    private String          userEmail    = "";
    private static final String STATIC_OTP = "123456";
    private CountDownTimer  countDownTimer;

    // ────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verify);

        // Receive email passed from ForgotPasswordActivity
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) userEmail = "";

        initViews();
        setupOtpAutoFocus();
        setupListeners();
        startCountdown();
    }

    // ── Bind every view to its XML id ────────────────────────────────────────
    private void initViews() {
        btnBack      = findViewById(R.id.btnBack);
        tvOtpEmail   = findViewById(R.id.tvOtpEmail);
        tvCountdown  = findViewById(R.id.tvCountdown);
        tvResendOtp  = findViewById(R.id.tvResendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);

        // Build array AFTER individual fields are assigned
        otpBoxes = new EditText[]{ etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6 };

        // Show the email the OTP was sent to
        tvOtpEmail.setText(userEmail);
    }

    // ── Auto-jump focus box → box as user types ───────────────────────────────
    private void setupOtpAutoFocus() {
        for (int i = 0; i < otpBoxes.length; i++) {
            final int idx = i;

            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        // Move forward
                        if (idx < otpBoxes.length - 1) {
                            otpBoxes[idx + 1].requestFocus();
                        }
                        // Auto-verify when last box filled
                        if (idx == otpBoxes.length - 1) {
                            new Handler().postDelayed(() -> verifyOtp(), 200);
                        }
                    }
                }
            });

            // Backspace on empty box → move back
            otpBoxes[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && otpBoxes[idx].getText().toString().isEmpty()
                        && idx > 0) {
                    otpBoxes[idx - 1].requestFocus();
                    otpBoxes[idx - 1].setText("");
                    return true;
                }
                return false;
            });
        }
    }

    // ── Button / link listeners ───────────────────────────────────────────────
    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        tvResendOtp.setOnClickListener(v -> {
            clearOtpBoxes();
            startCountdown();
            Toast.makeText(this,
                    "New OTP sent to " + userEmail, Toast.LENGTH_SHORT).show();
        });
    }

    // ── Collect OTP and validate ──────────────────────────────────────────────
    private void verifyOtp() {
        String entered = etOtp1.getText().toString().trim()
                + etOtp2.getText().toString().trim()
                + etOtp3.getText().toString().trim()
                + etOtp4.getText().toString().trim()
                + etOtp5.getText().toString().trim()
                + etOtp6.getText().toString().trim();

        if (entered.length() < 6) {
            Toast.makeText(this,
                    "Please enter the complete 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Verifying...");

        new Handler().postDelayed(() -> {
            if (entered.equals(STATIC_OTP)) {
                if (countDownTimer != null) countDownTimer.cancel();
                Toast.makeText(this, "OTP Verified! ✓", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpVerifyActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", userEmail);
                startActivity(intent);
                finish();
            } else {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("VERIFY OTP");
                clearOtpBoxes();
                Toast.makeText(this,
                        "Invalid OTP. Try again.\nHint: " + STATIC_OTP,
                        Toast.LENGTH_LONG).show();
            }
        }, 800);
    }

    // ── 2-minute countdown ────────────────────────────────────────────────────
    private void startCountdown() {
        if (countDownTimer != null) countDownTimer.cancel();

        // Disable resend until timer finishes
        tvResendOtp.setEnabled(false);
        tvResendOtp.setAlpha(0.4f);

        countDownTimer = new CountDownTimer(120_000, 1000) {
            @Override
            public void onTick(long ms) {
                long min = ms / 60000;
                long sec = (ms % 60000) / 1000;
                tvCountdown.setText(
                        String.format(Locale.getDefault(),
                                "OTP expires in: %02d:%02d", min, sec));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("OTP expired. Tap Resend.");
                tvResendOtp.setEnabled(true);
                tvResendOtp.setAlpha(1.0f);
            }
        }.start();
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private void clearOtpBoxes() {
        for (EditText box : otpBoxes) box.setText("");
        etOtp1.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}