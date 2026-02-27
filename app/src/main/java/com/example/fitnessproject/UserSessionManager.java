package com.example.fitnessproject;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    private static final String PREF_NAME = "FitLifePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PASSWORD = "userPassword";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_GOAL = "userGoal";
    private static final String KEY_USER_AGE = "userAge";
    private static final String KEY_USER_HEIGHT = "userHeight";
    private static final String KEY_USER_WEIGHT = "userWeight";
    private static final String KEY_USER_GENDER = "userGender";
    private static final String KEY_USER_ACTIVITY_LEVEL = "userActivityLevel";
    private static final String KEY_USER_BMI = "userBMI";
    private static final String KEY_PROFILE_COMPLETE = "profileComplete";
    private static final String KEY_DISCIPLINE_SCORE = "disciplineScore";
    private static final String KEY_WORKOUT_STREAK = "workoutStreak";
    private static final String KEY_TODAY_WORKOUT = "todayWorkout";
    private static final String KEY_WORKOUT_COMPLETED = "workoutCompleted";
    private static final String KEY_WORKOUT_TOTAL = "workoutTotal";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserSessionManager(Context ctx) {
        this.context = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(String name, String email, String password) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.commit();
    }

    public void createRegisterSession(String name, String email, String password, String phone, String goal) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_GOAL, goal);
        editor.putBoolean(KEY_PROFILE_COMPLETE, false);
        editor.commit();
    }

    public void saveProfile(String age, String height, String weight, String gender,
                            String goal, String activityLevel, float bmi) {
        editor.putString(KEY_USER_AGE, age);
        editor.putString(KEY_USER_HEIGHT, height);
        editor.putString(KEY_USER_WEIGHT, weight);
        editor.putString(KEY_USER_GENDER, gender);
        editor.putString(KEY_USER_GOAL, goal);
        editor.putString(KEY_USER_ACTIVITY_LEVEL, activityLevel);
        editor.putFloat(KEY_USER_BMI, bmi);
        editor.putBoolean(KEY_PROFILE_COMPLETE, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Athlete");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public boolean isProfileComplete() {
        return prefs.getBoolean(KEY_PROFILE_COMPLETE, false);
    }

    public void setProfileComplete(boolean status) {
        editor.putBoolean(KEY_PROFILE_COMPLETE, status);
        editor.commit();
    }

    public float getBMI() {
        return prefs.getFloat(KEY_USER_BMI, 0f);
    }

    public int getDisciplineScore() {
        return prefs.getInt(KEY_DISCIPLINE_SCORE, 87);
    }

    public void setDisciplineScore(int score) {
        editor.putInt(KEY_DISCIPLINE_SCORE, score);
        editor.commit();
    }

    public int getWorkoutStreak() {
        return prefs.getInt(KEY_WORKOUT_STREAK, 12);
    }

    public void setWorkoutStreak(int streak) {
        editor.putInt(KEY_WORKOUT_STREAK, streak);
        editor.commit();
    }

    public String getTodayWorkout() {
        return prefs.getString(KEY_TODAY_WORKOUT, "Upper Body Strength");
    }

    public void setTodayWorkout(String workout) {
        editor.putString(KEY_TODAY_WORKOUT, workout);
        editor.commit();
    }

    public int getWorkoutCompleted() {
        return prefs.getInt(KEY_WORKOUT_COMPLETED, 2);
    }

    public void setWorkoutCompleted(int completed) {
        editor.putInt(KEY_WORKOUT_COMPLETED, completed);
        editor.commit();
    }

    public int getWorkoutTotal() {
        return prefs.getInt(KEY_WORKOUT_TOTAL, 6);
    }

    public void setWorkoutTotal(int total) {
        editor.putInt(KEY_WORKOUT_TOTAL, total);
        editor.commit();
    }

    public void incrementWorkoutCompleted() {
        int current = getWorkoutCompleted();
        if (current < getWorkoutTotal()) {
            editor.putInt(KEY_WORKOUT_COMPLETED, current + 1);
            editor.commit();
        }
    }

    public void resetWorkoutProgress() {
        editor.putInt(KEY_WORKOUT_COMPLETED, 0);
        editor.commit();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}