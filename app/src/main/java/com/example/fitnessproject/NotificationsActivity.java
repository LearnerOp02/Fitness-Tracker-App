package com.example.fitnessproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;

public class NotificationsActivity extends BaseActivity {

    private Toolbar toolbar;
    private MaterialCardView workoutCard1, workoutCard2, healthCard1, healthCard2;
    private SwitchCompat sw1, sw2, sw3, sw4;
    private Button btnSaveNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        initViews();
        setupToolbar();
        loadPreferences();
        setupListeners();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
//        workoutCard1 = findViewById(R.id.workoutCard1);
//        workoutCard2 = findViewById(R.id.workoutCard2);
//        healthCard1 = findViewById(R.id.healthCard1);
//        healthCard2 = findViewById(R.id.healthCard2);
        sw1 = findViewById(R.id.sw1);
        sw2 = findViewById(R.id.sw2);
        sw3 = findViewById(R.id.sw3);
        sw4 = findViewById(R.id.sw4);
        btnSaveNotifications = findViewById(R.id.btnSaveNotifications);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Notifications", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(workoutCard1, 0);
        animateCard(workoutCard2, 100);
        animateCard(healthCard1, 200);
        animateCard(healthCard2, 300);
    }

    private void loadPreferences() {
        sw1.setChecked(sessionManager.getNotificationPref("notif_workout", true));
        sw2.setChecked(sessionManager.getNotificationPref("notif_streak", true));
        sw3.setChecked(sessionManager.getNotificationPref("notif_water", false));
        sw4.setChecked(sessionManager.getNotificationPref("notif_inactive", true));
    }

    private void setupListeners() {
        btnSaveNotifications.setOnClickListener(v -> {
            animateClick(v);
            sessionManager.setNotificationPref("notif_workout", sw1.isChecked());
            sessionManager.setNotificationPref("notif_streak", sw2.isChecked());
            sessionManager.setNotificationPref("notif_water", sw3.isChecked());
            sessionManager.setNotificationPref("notif_inactive", sw4.isChecked());

            showToast("Notification preferences saved ✓");
            finish();
        });
    }
}