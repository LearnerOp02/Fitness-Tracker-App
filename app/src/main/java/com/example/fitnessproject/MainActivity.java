package com.example.fitnessproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3500;
    private static final int ANIMATION_DURATION = 800;
    private UserSessionManager sessionManager;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize session manager
        initSessionManager();

        // Initialize views
        initViews();

        // Start animations
        startSplashAnimations();

        // Simulate loading progress
        simulateProgress();

        // Navigate after animations
        navigateAfterDelay();
    }

    private void initSessionManager() {
        try {
            if (getApplication() instanceof FitnessApplication) {
                sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
            } else {
                sessionManager = new UserSessionManager(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sessionManager = new UserSessionManager(this);
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.splashProgress);
        progressBar.setIndeterminate(true);

        // Hide content initially
        MaterialCardView contentCard = findViewById(R.id.contentCard);
        contentCard.setVisibility(View.INVISIBLE);
    }

    private void startSplashAnimations() {
        // Animate background circles
        animateBackgroundCircles();

        // Animate content card entrance
        animateContentCard();

        // Animate logo with bounce effect
        animateLogo();

        // Animate text sequentially
        animateText();
    }

    private void animateBackgroundCircles() {
        View circle1 = findViewById(R.id.circle1);
        View circle2 = findViewById(R.id.circle2);

        // Animate circle1
        ObjectAnimator circle1X = ObjectAnimator.ofFloat(circle1, "translationX", 0f, 50f, -30f, 0f);
        circle1X.setDuration(4000);
        circle1X.setRepeatCount(ValueAnimator.INFINITE);
        circle1X.setRepeatMode(ValueAnimator.REVERSE);
        circle1X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1X.start();

        ObjectAnimator circle1Y = ObjectAnimator.ofFloat(circle1, "translationY", 0f, -40f, 30f, 0f);
        circle1Y.setDuration(5000);
        circle1Y.setRepeatCount(ValueAnimator.INFINITE);
        circle1Y.setRepeatMode(ValueAnimator.REVERSE);
        circle1Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1Y.start();

        // Animate circle2
        ObjectAnimator circle2X = ObjectAnimator.ofFloat(circle2, "translationX", 0f, -40f, 40f, 0f);
        circle2X.setDuration(4500);
        circle2X.setRepeatCount(ValueAnimator.INFINITE);
        circle2X.setRepeatMode(ValueAnimator.REVERSE);
        circle2X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2X.start();
    }

    private void animateContentCard() {
        MaterialCardView contentCard = findViewById(R.id.contentCard);
        contentCard.setVisibility(View.VISIBLE);

        // Scale animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(contentCard, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(contentCard, "scaleY", 0.8f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(contentCard, "alpha", 0f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, alpha);
        set.setDuration(1000);
        set.setInterpolator(new AnticipateOvershootInterpolator(1.5f));
        set.start();
    }

    private void animateLogo() {
        ImageView logo = findViewById(R.id.splashLogo);
        View logoContainer = findViewById(R.id.logoContainer);
        View pulseView = findViewById(R.id.pulseView);

        // Logo rotation and scale
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(logo, "rotation", 0f, 360f);
        rotateAnim.setDuration(1500);
        rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(logo, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(logo, "scaleY", 0.5f, 1f);

        // Pulse effect
        pulseView.setVisibility(View.VISIBLE);
        ObjectAnimator pulseAlpha = ObjectAnimator.ofFloat(pulseView, "alpha", 0.5f, 0f);
        pulseAlpha.setDuration(1500);
        pulseAlpha.setRepeatCount(2);
        pulseAlpha.setInterpolator(new DecelerateInterpolator());

        AnimatorSet logoAnimSet = new AnimatorSet();
        logoAnimSet.playTogether(rotateAnim, scaleXAnim, scaleYAnim, pulseAlpha);
        logoAnimSet.setDuration(1500);
        logoAnimSet.setInterpolator(new FastOutSlowInInterpolator());
        logoAnimSet.start();
    }

    private void animateText() {
        TextView appName = findViewById(R.id.appName);
        TextView tagline = findViewById(R.id.tagline);

        Handler handler = new Handler(Looper.getMainLooper());

        // App name animation
        appName.setAlpha(0f);
        appName.setTranslationY(50f);

        ObjectAnimator nameAlpha = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        ObjectAnimator nameTranslation = ObjectAnimator.ofFloat(appName, "translationY", 50f, 0f);

        AnimatorSet nameAnim = new AnimatorSet();
        nameAnim.playTogether(nameAlpha, nameTranslation);
        nameAnim.setDuration(800);
        nameAnim.setStartDelay(500);
        nameAnim.setInterpolator(new DecelerateInterpolator());
        nameAnim.start();

        // Tagline animation with delay
        handler.postDelayed(() -> {
            tagline.setAlpha(0f);
            tagline.setTranslationY(30f);

            ObjectAnimator tagAlpha = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f);
            ObjectAnimator tagTranslation = ObjectAnimator.ofFloat(tagline, "translationY", 30f, 0f);

            AnimatorSet tagAnim = new AnimatorSet();
            tagAnim.playTogether(tagAlpha, tagTranslation);
            tagAnim.setDuration(600);
            tagAnim.setInterpolator(new DecelerateInterpolator());
            tagAnim.start();
        }, 900);
    }

    private void simulateProgress() {
        // Animate progress with value changes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ValueAnimator progressAnimator = ValueAnimator.ofInt(0, 100);
            progressAnimator.setDuration(SPLASH_DELAY - 500);
            progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            progressAnimator.addUpdateListener(animation -> {
                int progress = (int) animation.getAnimatedValue();
                progressBar.setProgress(progress, true);
            });
            progressAnimator.start();
        }
    }

    private void navigateAfterDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Add exit animation
            MaterialCardView contentCard = findViewById(R.id.contentCard);
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(contentCard, "alpha", 1f, 0f);
            fadeOut.setDuration(300);
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    navigateNext();
                }
            });
            fadeOut.start();
        }, SPLASH_DELAY);
    }

    private void navigateNext() {
        try {
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

            // Add activity transition animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                startActivity(intent);
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}