package com.example.fitnessproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VisualProgressActivity extends AppCompatActivity {

    private static final int REQ_START_PHOTO = 1001;
    private static final int REQ_END_PHOTO   = 1002;

    private ImageButton btnBack, btnUploadStart, btnUploadEnd;
    private ImageView   ivStartPhoto, ivEndPhoto;
    private TextView    tvStartDate, tvEndDate,
            tvWeightChange, tvBmiChange, tvWorkoutsThisMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visual_progress);
        initViews();
        loadSavedPhotos();
        loadNumericalData();
        setupListeners();
    }

    private void initViews() {
        btnBack             = findViewById(R.id.btnBack);
        btnUploadStart      = findViewById(R.id.btnUploadStart);
        btnUploadEnd        = findViewById(R.id.btnUploadEnd);
        ivStartPhoto        = findViewById(R.id.ivStartPhoto);
        ivEndPhoto          = findViewById(R.id.ivEndPhoto);
        tvStartDate         = findViewById(R.id.tvStartDate);
        tvEndDate           = findViewById(R.id.tvEndDate);
        tvWeightChange      = findViewById(R.id.tvWeightChange);
        tvBmiChange         = findViewById(R.id.tvBmiChange);
        tvWorkoutsThisMonth = findViewById(R.id.tvWorkoutsThisMonth);
    }

    private void loadSavedPhotos() {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        String startUri = prefs.getString("startPhotoUri", "");
        String endUri   = prefs.getString("endPhotoUri",   "");
        String startDt  = prefs.getString("startPhotoDate", "Not uploaded");
        String endDt    = prefs.getString("endPhotoDate",   "Not uploaded");

        if (!startUri.isEmpty()) {
            try { ivStartPhoto.setImageURI(Uri.parse(startUri)); } catch (Exception ignored) {}
        }
        if (!endUri.isEmpty()) {
            try { ivEndPhoto.setImageURI(Uri.parse(endUri)); } catch (Exception ignored) {}
        }
        tvStartDate.setText(startDt);
        tvEndDate.setText(endDt);
    }

    private void loadNumericalData() {
        SharedPreferences prefs = getSharedPreferences("FitLifePrefs", MODE_PRIVATE);
        float currentBmi    = prefs.getFloat("userBMI", 0f);
        float prevBmi       = prefs.getFloat("prevMonthBMI", 0f);
        float currentWeight = 0, prevWeight = 0;

        try {
            currentWeight = Float.parseFloat(prefs.getString("userWeight", "0"));
            prevWeight    = prefs.getFloat("prevMonthWeight", currentWeight);
        } catch (Exception ignored) {}

        // Weight change
        float wDiff = currentWeight - prevWeight;
        String wStr = wDiff == 0 ? "No change" : String.format("%+.1f kg", wDiff);
        tvWeightChange.setText(wStr);
        tvWeightChange.setTextColor(wDiff <= 0 ? 0xFF4CAF50 : 0xFFF44336);

        // BMI change
        if (currentBmi > 0 && prevBmi > 0) {
            float bDiff = currentBmi - prevBmi;
            tvBmiChange.setText(String.format("%+.1f pts", bDiff));
            tvBmiChange.setTextColor(bDiff <= 0 ? 0xFF4CAF50 : 0xFFF44336);
        } else {
            tvBmiChange.setText("N/A");
        }

        // Count this month's workouts
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        int count = 0;
        try {
            JSONArray arr = new JSONArray(prefs.getString("workoutHistory", "[]"));
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).optString("date", "").startsWith(currentMonth)) count++;
            }
        } catch (Exception ignored) {}
        tvWorkoutsThisMonth.setText(String.valueOf(count));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnUploadStart.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, REQ_START_PHOTO);
        });

        btnUploadEnd.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, REQ_END_PHOTO);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) return;

        Uri uri = data.getData();
        if (uri == null) return;

        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        SharedPreferences.Editor editor = getSharedPreferences("FitLifePrefs", MODE_PRIVATE).edit();

        if (requestCode == REQ_START_PHOTO) {
            ivStartPhoto.setImageURI(uri);
            tvStartDate.setText(date);
            editor.putString("startPhotoUri", uri.toString())
                    .putString("startPhotoDate", date);
        } else if (requestCode == REQ_END_PHOTO) {
            ivEndPhoto.setImageURI(uri);
            tvEndDate.setText(date);
            editor.putString("endPhotoUri", uri.toString())
                    .putString("endPhotoDate", date);
        }
        editor.apply();
        Toast.makeText(this, "Photo saved! ✓", Toast.LENGTH_SHORT).show();
    }
}