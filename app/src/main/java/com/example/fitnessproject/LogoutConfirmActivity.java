package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;

public class LogoutConfirmActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView logoutCard;
    private Button btnConfirmLogout, btnCancelLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_confirm);

        initViews();
        setupToolbar();
        startAnimations();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        // Note: The XML uses card_logout as ID, not logoutCard
//         = findViewById(R.id.card_logout);
        btnConfirmLogout = findViewById(R.id.btnConfirmLogout);
        btnCancelLogout = findViewById(R.id.btnCancelLogout);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Log Out", true);
    }

    private void startAnimations() {
        animateCard(logoutCard, 0);
    }

    private void setupListeners() {
        btnCancelLogout.setOnClickListener(v -> {
            animateClick(v);
            finish();
        });

        btnConfirmLogout.setOnClickListener(v -> {
            animateClick(v);
            sessionManager.logout();

            Intent intent = new Intent(LogoutConfirmActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }
}