package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class OtpVerifyActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView otpCard;
    private TextView tvOtpEmail, tvCountdown, tvResendOtp, tvStepIndicator;
    private Button btnVerifyOtp;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;

    private EditText[] otpBoxes;
    private String userEmail = "";
    private static final String STATIC_OTP = "123456";
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verify);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) userEmail = "";

        initViews();
        setupToolbar();
        setupOtpAutoFocus();
        setupListeners();
        startCountdown();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
        otpCard = findViewById(R.id.otpCard);
        tvOtpEmail = findViewById(R.id.tvOtpEmail);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvStepIndicator = findViewById(R.id.tvStepIndicator);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);

        otpBoxes = new EditText[]{ etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6 };
        tvOtpEmail.setText(userEmail);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Verify OTP", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(otpCard, 0);
    }

    private void setupOtpAutoFocus() {
        for (int i = 0; i < otpBoxes.length; i++) {
            final int idx = i;

            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        if (idx < otpBoxes.length - 1) {
                            otpBoxes[idx + 1].requestFocus();
                        }
                        if (idx == otpBoxes.length - 1) {
                            handler.postDelayed(() -> verifyOtp(), 200);
                        }
                    }
                }
            });

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

    private void setupListeners() {
        btnVerifyOtp.setOnClickListener(v -> {
            animateClick(v);
            verifyOtp();
        });

        tvResendOtp.setOnClickListener(v -> {
            animateClick(v);
            clearOtpBoxes();
            startCountdown();
            showToast("New OTP sent to " + userEmail);
        });
    }

    private void verifyOtp() {
        String entered = etOtp1.getText().toString().trim()
                + etOtp2.getText().toString().trim()
                + etOtp3.getText().toString().trim()
                + etOtp4.getText().toString().trim()
                + etOtp5.getText().toString().trim()
                + etOtp6.getText().toString().trim();

        if (entered.length() < 6) {
            showToast("Please enter the complete 6-digit OTP");
            return;
        }

        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Verifying...");

        handler.postDelayed(() -> {
            if (entered.equals(STATIC_OTP)) {
                if (countDownTimer != null) countDownTimer.cancel();
                showToast("OTP Verified! ✓");
                Intent intent = new Intent(OtpVerifyActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", userEmail);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("VERIFY OTP");
                clearOtpBoxes();
                showLongToast("Invalid OTP. Try again.\nHint: " + STATIC_OTP);
            }
        }, 800);
    }

    private void startCountdown() {
        if (countDownTimer != null) countDownTimer.cancel();

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