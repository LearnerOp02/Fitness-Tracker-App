package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;

public class ResetSuccessActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView successCard;
    private ImageView ivSuccessIcon;
    private Button btnGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_success);

        initViews();
        setupToolbar();
        startAnimations();
        setupListeners();
        autoRedirect();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
//        successCard = findViewById(R.id.successCard);
        ivSuccessIcon = findViewById(R.id.ivSuccessIcon);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Success", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(successCard, 0);

        // Bounce animation for icon
        ScaleAnimation bounce = new ScaleAnimation(
                0f, 1.1f, 0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        bounce.setDuration(500);
        bounce.setFillAfter(true);

        ScaleAnimation settle = new ScaleAnimation(
                1.1f, 1.0f, 1.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        settle.setDuration(150);
        settle.setStartOffset(500);
        settle.setFillAfter(true);

        ivSuccessIcon.startAnimation(bounce);
        handler.postDelayed(() -> ivSuccessIcon.startAnimation(settle), 500);

        // Fade in button
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(600);
        fadeIn.setStartOffset(700);
        fadeIn.setFillAfter(true);
        btnGoToLogin.startAnimation(fadeIn);
    }

    private void setupListeners() {
        btnGoToLogin.setOnClickListener(v -> {
            animateClick(v);
            goToLogin();
        });
    }

    private void autoRedirect() {
        handler.postDelayed(this::goToLogin, 4000);
    }

    private void goToLogin() {
        startActivity(new Intent(ResetSuccessActivity.this, LoginActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}