package com.example.fitnessproject;

import android.app.Activity;
import android.content.Intent;
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
    private static final int REQ_END_PHOTO = 1002;

    private ImageButton btnBack, btnUploadStart, btnUploadEnd;
    private ImageView ivStartPhoto, ivEndPhoto;
    private TextView tvStartDate, tvEndDate,
            tvWeightChange, tvBmiChange, tvWorkoutsThisMonth;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visual_progress);

        sessionManager = ((FitnessApplication) getApplication()).getSessionManager();
        initViews();
        loadSavedPhotos();
        loadNumericalData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnUploadStart = findViewById(R.id.btnUploadStart);
        btnUploadEnd = findViewById(R.id.btnUploadEnd);
        ivStartPhoto = findViewById(R.id.ivStartPhoto);
        ivEndPhoto = findViewById(R.id.ivEndPhoto);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvWeightChange = findViewById(R.id.tvWeightChange);
        tvBmiChange = findViewById(R.id.tvBmiChange);
        tvWorkoutsThisMonth = findViewById(R.id.tvWorkoutsThisMonth);
    }

    private void loadSavedPhotos() {
        String startUri = sessionManager.getPhotoUri("startPhotoUri");
        String endUri = sessionManager.getPhotoUri("endPhotoUri");
        String startDt = sessionManager.getPhotoDate("startPhotoDate");
        String endDt = sessionManager.getPhotoDate("endPhotoDate");

        if (!startUri.isEmpty()) {
            try {
                ivStartPhoto.setImageURI(Uri.parse(startUri));
            } catch (Exception ignored) {
            }
        }
        if (!endUri.isEmpty()) {
            try {
                ivEndPhoto.setImageURI(Uri.parse(endUri));
            } catch (Exception ignored) {
            }
        }
        tvStartDate.setText(startDt);
        tvEndDate.setText(endDt);
    }

    private void loadNumericalData() {
        float currentBmi = sessionManager.getBMI();
        float prevBmi = sessionManager.getPrevMonthBMI();
        float currentWeight = 0, prevWeight = 0;

        try {
            currentWeight = Float.parseFloat(sessionManager.getUserWeight());
            prevWeight = sessionManager.getPrevMonthWeight();
            if (prevWeight == 0) prevWeight = currentWeight;
        } catch (Exception ignored) {
        }

        float wDiff = currentWeight - prevWeight;
        String wStr = wDiff == 0 ? "No change" : String.format("%+.1f kg", wDiff);
        tvWeightChange.setText(wStr);
        tvWeightChange.setTextColor(wDiff <= 0 ? 0xFF4CAF50 : 0xFFF44336);

        if (currentBmi > 0 && prevBmi > 0) {
            float bDiff = currentBmi - prevBmi;
            tvBmiChange.setText(String.format("%+.1f pts", bDiff));
            tvBmiChange.setTextColor(bDiff <= 0 ? 0xFF4CAF50 : 0xFFF44336);
        } else {
            tvBmiChange.setText("N/A");
        }

        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        int count = 0;
        try {
            JSONArray arr = new JSONArray(sessionManager.getWorkoutHistory());
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).optString("date", "").startsWith(currentMonth)) count++;
            }
        } catch (Exception ignored) {
        }
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

        if (requestCode == REQ_START_PHOTO) {
            ivStartPhoto.setImageURI(uri);
            tvStartDate.setText(date);
            sessionManager.savePhotoUri("startPhotoUri", uri.toString());
            sessionManager.savePhotoDate("startPhotoDate", date);
        } else if (requestCode == REQ_END_PHOTO) {
            ivEndPhoto.setImageURI(uri);
            tvEndDate.setText(date);
            sessionManager.savePhotoUri("endPhotoUri", uri.toString());
            sessionManager.savePhotoDate("endPhotoDate", date);
        }
        Toast.makeText(this, "Photo saved! ✓", Toast.LENGTH_SHORT).show();
    }
}