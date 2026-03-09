package com.example.fitnessproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VisualProgressActivity extends BaseActivity {

    private static final int REQ_START_PHOTO = 1001;
    private static final int REQ_END_PHOTO = 1002;

    private Toolbar toolbar;
    private MaterialCardView startPhotoCard, endPhotoCard, summaryCard;
    private ImageView ivStartPhoto, ivEndPhoto;
    private FloatingActionButton btnUploadStart, btnUploadEnd;
    private TextView tvStartDate, tvEndDate, tvWeightChange, tvBmiChange, tvWorkoutsThisMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visual_progress);

        initViews();
        setupToolbar();
        loadSavedPhotos();
        loadNumericalData();
        setupListeners();
        startAnimations();
    }

    private void initViews() {
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        toolbar = findViewById(R.id.toolbar);
//        startPhotoCard = findViewById(R.id.startPhotoCard);
//        endPhotoCard = findViewById(R.id.endPhotoCard);
//        summaryCard = findViewById(R.id.summaryCard);
        ivStartPhoto = findViewById(R.id.ivStartPhoto);
        ivEndPhoto = findViewById(R.id.ivEndPhoto);
        btnUploadStart = findViewById(R.id.btnUploadStart);
        btnUploadEnd = findViewById(R.id.btnUploadEnd);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvWeightChange = findViewById(R.id.tvWeightChange);
        tvBmiChange = findViewById(R.id.tvBmiChange);
        tvWorkoutsThisMonth = findViewById(R.id.tvWorkoutsThisMonth);
    }

    private void setupToolbar() {
        setupToolbar(toolbar, "Visual Progress", true);
    }

    private void startAnimations() {
        animateBackgroundCircles();
        animateCard(startPhotoCard, 0);
        animateCard(endPhotoCard, 150);
        animateCard(summaryCard, 300);
    }

    private void loadSavedPhotos() {
        String startUri = sessionManager.getPhotoUri("startPhotoUri");
        String endUri = sessionManager.getPhotoUri("endPhotoUri");
        String startDt = sessionManager.getPhotoDate("startPhotoDate");
        String endDt = sessionManager.getPhotoDate("endPhotoDate");

        if (!startUri.isEmpty()) {
            try {
                ivStartPhoto.setImageURI(Uri.parse(startUri));
            } catch (Exception ignored) {}
        }
        if (!endUri.isEmpty()) {
            try {
                ivEndPhoto.setImageURI(Uri.parse(endUri));
            } catch (Exception ignored) {}
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
        } catch (Exception ignored) {}

        float wDiff = currentWeight - prevWeight;
        String wStr = wDiff == 0 ? "No change" : String.format("%+.1f kg", wDiff);
        tvWeightChange.setText(wStr);
        tvWeightChange.setTextColor(wDiff <= 0 ?
                getResources().getColor(R.color.normal) :
                getResources().getColor(R.color.obese));

        if (currentBmi > 0 && prevBmi > 0) {
            float bDiff = currentBmi - prevBmi;
            tvBmiChange.setText(String.format("%+.1f pts", bDiff));
            tvBmiChange.setTextColor(bDiff <= 0 ?
                    getResources().getColor(R.color.normal) :
                    getResources().getColor(R.color.obese));
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
        } catch (Exception ignored) {}
        tvWorkoutsThisMonth.setText(String.valueOf(count));
    }

    private void setupListeners() {
        btnUploadStart.setOnClickListener(v -> {
            animateClick(v);
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, REQ_START_PHOTO);
        });

        btnUploadEnd.setOnClickListener(v -> {
            animateClick(v);
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
        showToast("Photo saved! ✓");
    }
}