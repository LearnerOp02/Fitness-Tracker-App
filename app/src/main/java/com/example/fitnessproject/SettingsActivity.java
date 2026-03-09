package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView accountCard, preferencesCard, infoCard;
    private RelativeLayout rowUpdateProfile, rowChangePassword, rowNotifications, rowBmi;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initViews();
        setupToolbar();
        setupListeners();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
//        accountCard = findViewById(R.id.accountCard);
//        preferencesCard = findViewById(R.id.preferencesCard);
//        infoCard = findViewById(R.id.infoCard);
        rowUpdateProfile = findViewById(R.id.rowUpdateProfile);
        rowChangePassword = findViewById(R.id.rowChangePassword);
        rowNotifications = findViewById(R.id.rowNotifications);
        rowBmi = findViewById(R.id.rowBmi);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Settings", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(accountCard, 0);
        animateCard(preferencesCard, 150);
        animateCard(infoCard, 300);
    }

    private void setupListeners() {
        rowUpdateProfile.setOnClickListener(v -> {
            animateClick(v);
            navigateTo(ProfileSetupActivity.class);
        });

        rowChangePassword.setOnClickListener(v -> {
            animateClick(v);
            navigateTo(ForgetPasswordActivity.class);
        });

        rowNotifications.setOnClickListener(v -> {
            animateClick(v);
            navigateTo(NotificationsActivity.class);
        });

        rowBmi.setOnClickListener(v -> {
            animateClick(v);
            navigateTo(BmiCalculatorActivity.class);
        });

        btnLogout.setOnClickListener(v -> {
            animateClick(v);
            navigateTo(LogoutConfirmActivity.class);
        });
    }
}