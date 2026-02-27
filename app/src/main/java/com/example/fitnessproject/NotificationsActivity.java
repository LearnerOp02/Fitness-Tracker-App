package com.example.fitnessproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Switch sw1, sw2, sw3, sw4;
    private Button btnSaveNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        initViews();
        loadPreferences();
        setupListeners();
    }

    private void initViews() {
        btnBack               = findViewById(R.id.btnBack);
        sw1                   = findViewById(R.id.sw1);
        sw2                   = findViewById(R.id.sw2);
        sw3                   = findViewById(R.id.sw3);
        sw4                   = findViewById(R.id.sw4);
        btnSaveNotifications  = findViewById(R.id.btnSaveNotifications);
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        sw1.setChecked(prefs.getBoolean("notif_workout", true));
        sw2.setChecked(prefs.getBoolean("notif_streak",  true));
        sw3.setChecked(prefs.getBoolean("notif_water",   false));
        sw4.setChecked(prefs.getBoolean("notif_inactive",true));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSaveNotifications.setOnClickListener(v -> {
            getSharedPreferences("FitLifePrefs", MODE_PRIVATE).edit()
                    .putBoolean("notif_workout",  sw1.isChecked())
                    .putBoolean("notif_streak",   sw2.isChecked())
                    .putBoolean("notif_water",    sw3.isChecked())
                    .putBoolean("notif_inactive", sw4.isChecked())
                    .apply();
            Toast.makeText(this, "Notification preferences saved ✓", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}