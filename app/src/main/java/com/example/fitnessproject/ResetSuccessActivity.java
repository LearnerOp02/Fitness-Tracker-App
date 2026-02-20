package com.example.fitnessproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ResetSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_success);

        ImageView ivSuccessIcon = findViewById(R.id.ivSuccessIcon);
        Button btnGoToLogin     = findViewById(R.id.btnGoToLogin);

        // Bounce-in animation for the success icon
        ScaleAnimation bounce = new ScaleAnimation(
                0f, 1.1f, 0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        bounce.setDuration(500);
        bounce.setFillAfter(true);

        // Settle back to normal size
        ScaleAnimation settle = new ScaleAnimation(
                1.1f, 1.0f, 1.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        settle.setDuration(150);
        settle.setStartOffset(500);
        settle.setFillAfter(true);

        ivSuccessIcon.startAnimation(bounce);
        new Handler().postDelayed(() -> ivSuccessIcon.startAnimation(settle), 500);

        // Fade in button
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(600);
        fadeIn.setStartOffset(700);
        fadeIn.setFillAfter(true);
        btnGoToLogin.startAnimation(fadeIn);

        btnGoToLogin.setOnClickListener(v -> goToLogin());

        // Auto redirect to login after 4 seconds
        new Handler().postDelayed(this::goToLogin, 4000);
    }

    private void goToLogin() {
        startActivity(new Intent(ResetSuccessActivity.this, LoginActivity.class));
        finish();
    }
}