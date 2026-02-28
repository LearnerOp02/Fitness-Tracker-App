package com.example.fitnessproject;

import android.content.Intent;
import android.content.SharedPreferences;
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

        // Initialize views
        ImageView logo = findViewById(R.id.splashLogo);
        TextView appName = findViewById(R.id.appName);
        TextView tagline = findViewById(R.id.tagline);

        // Check if views exist (safety check)
        if (logo == null || appName == null || tagline == null) {
            // If views are missing, navigate directly
            new Handler().postDelayed(this::navigateNext, 100);
            return;
        }

        try {
            // Initialize session manager
            if (getApplication() instanceof FitnessApplication) {
                sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
            } else {
                sessionManager = new UserSessionManager(this);
            }

            // Apply animations
            ScaleAnimation scaleAnim = new ScaleAnimation(
                    0f, 1f, 0f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnim.setDuration(800);
            scaleAnim.setFillAfter(true);
            logo.startAnimation(scaleAnim);

            AlphaAnimation fadeInName = new AlphaAnimation(0f, 1f);
            fadeInName.setDuration(1000);
            fadeInName.setStartOffset(600);
            fadeInName.setFillAfter(true);
            appName.startAnimation(fadeInName);

            AlphaAnimation fadeInTag = new AlphaAnimation(0f, 1f);
            fadeInTag.setDuration(1000);
            fadeInTag.setStartOffset(1000);
            fadeInTag.setFillAfter(true);
            tagline.startAnimation(fadeInTag);

        } catch (Exception e) {
            e.printStackTrace();
            // If any error, just navigate
            new Handler().postDelayed(this::navigateNext, 100);
            return;
        }

        // Navigate after delay
        new Handler().postDelayed(this::navigateNext, SPLASH_DELAY);
    }

    private void navigateNext() {
        try {
            if (sessionManager == null) {
                sessionManager = new UserSessionManager(this);
            }

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
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}