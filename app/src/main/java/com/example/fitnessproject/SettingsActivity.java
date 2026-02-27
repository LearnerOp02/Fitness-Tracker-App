package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.rowUpdateProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileSetupActivity.class)));

        findViewById(R.id.rowChangePassword).setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.rowNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        findViewById(R.id.rowBmi).setOnClickListener(v ->
                startActivity(new Intent(this, BmiCalculatorActivity.class)));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LogoutConfirmActivity.class);
            startActivity(intent);
        });
    }
}