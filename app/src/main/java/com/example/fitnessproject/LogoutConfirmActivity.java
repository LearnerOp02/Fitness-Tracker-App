package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LogoutConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transparent window so background activity shows through
        setContentView(R.layout.logout_confirm);

        Button btnConfirm = findViewById(R.id.btnConfirmLogout);
        Button btnCancel  = findViewById(R.id.btnCancelLogout);

        btnCancel.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            // Clear login state
            SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("isLoggedIn", false).apply();

            // Go to Login and clear back stack
            Intent intent = new Intent(LogoutConfirmActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}