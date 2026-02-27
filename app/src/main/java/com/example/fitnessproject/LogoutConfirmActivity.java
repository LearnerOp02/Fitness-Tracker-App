package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LogoutConfirmActivity extends AppCompatActivity {

    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_confirm);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();

        Button btnConfirm = findViewById(R.id.btnConfirmLogout);
        Button btnCancel = findViewById(R.id.btnCancelLogout);

        btnCancel.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            sessionManager.logout();

            Intent intent = new Intent(LogoutConfirmActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}