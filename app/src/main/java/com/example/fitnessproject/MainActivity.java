package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();

        ImageView logo = findViewById(R.id.splashLogo);
        TextView appName = findViewById(R.id.appName);
        TextView tagline = findViewById(R.id.tagline);

        // Scale animation for logo
        ScaleAnimation scaleAnim = new ScaleAnimation(
                0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(800);
        scaleAnim.setFillAfter(true);
        logo.startAnimation(scaleAnim);

        // Fade in app name
        AlphaAnimation fadeInName = new AlphaAnimation(0f, 1f);
        fadeInName.setDuration(1000);
        fadeInName.setStartOffset(600);
        fadeInName.setFillAfter(true);
        appName.startAnimation(fadeInName);

        // Fade in tagline
        AlphaAnimation fadeInTag = new AlphaAnimation(0f, 1f);
        fadeInTag.setDuration(1000);
        fadeInTag.setStartOffset(1000);
        fadeInTag.setFillAfter(true);
        tagline.startAnimation(fadeInTag);

        // After delay → decide where to go
        new Handler().postDelayed(this::navigateNext, SPLASH_DELAY);
    }

    private void navigateNext() {
        boolean isLoggedIn = sessionManager.isLoggedIn();
        boolean profileComplete = sessionManager.isProfileComplete();

        Intent intent;
        if (isLoggedIn && profileComplete) {
            intent = new Intent(this, HomeActivity.class);
        } else if (isLoggedIn) {
            intent = new Intent(this, ProfileSetupActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}