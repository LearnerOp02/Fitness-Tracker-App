package com.example.fitnessproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

    protected UserSessionManager sessionManager;
    protected boolean backPressedOnce = false;
    protected View circle1, circle2;
    protected Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSessionManager();
    }

    protected void initSessionManager() {
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

    protected void animateBackgroundCircles() {
        if (circle1 == null || circle2 == null) return;

        // Circle 1 animation
        ObjectAnimator circle1X = ObjectAnimator.ofFloat(circle1, "translationX", 0f, 50f, -30f, 0f);
        circle1X.setDuration(4000);
        circle1X.setRepeatCount(ObjectAnimator.INFINITE);
        circle1X.setRepeatMode(ObjectAnimator.REVERSE);
        circle1X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1X.start();

        ObjectAnimator circle1Y = ObjectAnimator.ofFloat(circle1, "translationY", 0f, -40f, 30f, 0f);
        circle1Y.setDuration(5000);
        circle1Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle1Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle1Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1Y.start();

        // Circle 2 animation
        ObjectAnimator circle2X = ObjectAnimator.ofFloat(circle2, "translationX", 0f, -40f, 40f, 0f);
        circle2X.setDuration(4500);
        circle2X.setRepeatCount(ObjectAnimator.INFINITE);
        circle2X.setRepeatMode(ObjectAnimator.REVERSE);
        circle2X.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2X.start();

        ObjectAnimator circle2Y = ObjectAnimator.ofFloat(circle2, "translationY", 0f, 30f, -20f, 0f);
        circle2Y.setDuration(4800);
        circle2Y.setRepeatCount(ObjectAnimator.INFINITE);
        circle2Y.setRepeatMode(ObjectAnimator.REVERSE);
        circle2Y.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2Y.start();
    }

    protected void animateCard(View card, long delay) {
        if (card == null) return;

        card.setAlpha(0f);
        card.setTranslationY(50f);
        card.setScaleX(0.95f);
        card.setScaleY(0.95f);

        handler.postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f);
            ObjectAnimator translation = ObjectAnimator.ofFloat(card, "translationY", 50f, 0f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(card, "scaleX", 0.95f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(card, "scaleY", 0.95f, 1f);

            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(alpha, translation, scaleX, scaleY);
            animSet.setDuration(600);
            animSet.setInterpolator(new DecelerateInterpolator());
            animSet.start();
        }, delay);
    }

    protected void animateClick(View view) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start())
                .start();
    }

    protected void setupToolbar(Toolbar toolbar, String title, boolean showBack) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(20f);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setGravity(android.view.Gravity.CENTER);

        toolbar.addView(titleView, new Toolbar.LayoutParams(
                android.view.Gravity.CENTER));

        if (showBack) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void navigateWithClear(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

//    @Override
//    public void onBackPressed() {
//        if (backPressedOnce) {
//            super.onBackPressed();
//            finishAffinity();
//            return;
//        }
//
//        backPressedOnce = true;
//        showToast("Press back again to exit");
//
//        handler.postDelayed(() -> backPressedOnce = false, 2000);
//    }
}