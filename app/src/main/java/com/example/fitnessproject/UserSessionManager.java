package com.example.fitnessproject;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static final String KEY_WORKOUT_HISTORY = "workoutHistory";
    private static final String KEY_LAST_WORKOUT_DATE = "lastWorkoutDate";
    private static final String KEY_WORKOUT_LOG = "workoutLog";
    private static final String KEY_COMPLETION_RATE = "completionRate";
    private static final String KEY_GOAL_ACHIEVEMENT = "goalAchievement";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserSessionManager(Context ctx) {
        this.context = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Initialize default values if not set
        if (!prefs.contains(KEY_DISCIPLINE_SCORE)) {
            editor.putInt(KEY_DISCIPLINE_SCORE, 75);
        }
        if (!prefs.contains(KEY_WORKOUT_STREAK)) {
            editor.putInt(KEY_WORKOUT_STREAK, 0);
        }
        if (!prefs.contains(KEY_TODAY_WORKOUT)) {
            editor.putString(KEY_TODAY_WORKOUT, "Upper Body Strength");
        }
        if (!prefs.contains(KEY_WORKOUT_TOTAL)) {
            editor.putInt(KEY_WORKOUT_TOTAL, 6);
        }
        if (!prefs.contains(KEY_WORKOUT_HISTORY)) {
            editor.putString(KEY_WORKOUT_HISTORY, "[]");
        }
        if (!prefs.contains(KEY_WORKOUT_LOG)) {
            editor.putString(KEY_WORKOUT_LOG, "[]");
        }
        editor.apply();
    }

    // ==================== AUTHENTICATION METHODS ====================

    public void createLoginSession(String name, String email, String password) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.apply();

        // Initialize discipline score for new user
        if (getDisciplineScore() == 0) {
            setDisciplineScore(75);
        }
    }

    public void createRegisterSession(String name, String email, String password, String phone, String goal) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_GOAL, goal);
        editor.putBoolean(KEY_PROFILE_COMPLETE, false);
        editor.putInt(KEY_DISCIPLINE_SCORE, 75); // Initial discipline score
        editor.putInt(KEY_WORKOUT_STREAK, 0);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    public void clearAll() {
        editor.clear();
        editor.apply();
    }

    // ==================== USER PROFILE METHODS ====================

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Athlete");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPassword() {
        return prefs.getString(KEY_USER_PASSWORD, "");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    public String getUserGoal() {
        return prefs.getString(KEY_USER_GOAL, "Build Muscle");
    }

    public String getUserAge() {
        return prefs.getString(KEY_USER_AGE, "25");
    }

    public String getUserHeight() {
        return prefs.getString(KEY_USER_HEIGHT, "");
    }

    public String getUserWeight() {
        return prefs.getString(KEY_USER_WEIGHT, "");
    }

    public String getUserGender() {
        return prefs.getString(KEY_USER_GENDER, "Male");
    }

    public String getUserActivityLevel() {
        return prefs.getString(KEY_USER_ACTIVITY_LEVEL, "Moderately Active");
    }

    public boolean isProfileComplete() {
        return prefs.getBoolean(KEY_PROFILE_COMPLETE, false);
    }

    public void setProfileComplete(boolean status) {
        editor.putBoolean(KEY_PROFILE_COMPLETE, status);
        editor.apply();
    }

    public float getBMI() {
        return prefs.getFloat(KEY_USER_BMI, 0f);
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
        editor.apply();
    }

    // ==================== DISCIPLINE SCORE METHODS ====================

    public int getDisciplineScore() {
        return prefs.getInt(KEY_DISCIPLINE_SCORE, 75);
    }

    public void setDisciplineScore(int score) {
        editor.putInt(KEY_DISCIPLINE_SCORE, Math.min(100, Math.max(0, score)));
        editor.apply();
    }

    public void incrementDisciplineScore(int increment) {
        int current = getDisciplineScore();
        setDisciplineScore(current + increment);
    }

    public void decrementDisciplineScore(int decrement) {
        int current = getDisciplineScore();
        setDisciplineScore(current - decrement);
    }

    public void calculateAndUpdateDisciplineScore() {
        float completionRate = getCompletionRateLast2Weeks() / 100f;
        float streakConsistency = calculateStreakConsistency();
        float goalAchievement = getGoalAchievementProgress();

        // Formula: (Workout Completion Rate × 0.5) + (Streak Consistency × 0.3) + (Goal Achievement Progress × 0.2)
        int newScore = (int) ((completionRate * 50) + (streakConsistency * 30) + (goalAchievement * 20));

        // Ensure score is between 0 and 100
        newScore = Math.min(100, Math.max(0, newScore));

        setDisciplineScore(newScore);
        setGoalAchievementProgress(goalAchievement); // Store for reference
    }

    private float calculateStreakConsistency() {
        int streak = getWorkoutStreak();
        // Consistency based on streak (max 30 days considered perfect)
        return Math.min(1.0f, streak / 30.0f);
    }

    // ==================== WORKOUT STREAK METHODS ====================

    public int getWorkoutStreak() {
        return prefs.getInt(KEY_WORKOUT_STREAK, 0);
    }

    public void setWorkoutStreak(int streak) {
        editor.putInt(KEY_WORKOUT_STREAK, streak);
        editor.apply();
    }

    public void incrementWorkoutStreak() {
        setWorkoutStreak(getWorkoutStreak() + 1);
        calculateAndUpdateDisciplineScore();
    }

    public void resetWorkoutStreak() {
        setWorkoutStreak(0);
        calculateAndUpdateDisciplineScore();
    }

    public void updateStreakBasedOnLastWorkout() {
        String lastDateStr = prefs.getString(KEY_LAST_WORKOUT_DATE, "");
        if (lastDateStr.isEmpty()) {
            // First workout, set streak to 1
            setWorkoutStreak(1);
            updateLastWorkoutDate();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date lastDate = sdf.parse(lastDateStr);
            Date today = new Date();

            Calendar cal = Calendar.getInstance();
            cal.setTime(lastDate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date nextDay = cal.getTime();

            if (isSameDay(today, lastDate)) {
                // Already logged today, don't change streak
                return;
            } else if (isSameDay(today, nextDay)) {
                // Consecutive day, increment streak
                incrementWorkoutStreak();
            } else {
                // Missed a day, reset streak
                resetWorkoutStreak();
                setWorkoutStreak(1); // Start new streak
            }

            updateLastWorkoutDate();

        } catch (Exception e) {
            e.printStackTrace();
            setWorkoutStreak(1);
            updateLastWorkoutDate();
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void updateLastWorkoutDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editor.putString(KEY_LAST_WORKOUT_DATE, sdf.format(new Date()));
        editor.apply();
    }

    // ==================== WORKOUT PROGRESS METHODS ====================

    public String getTodayWorkout() {
        return prefs.getString(KEY_TODAY_WORKOUT, "Upper Body Strength");
    }

    public void setTodayWorkout(String workout) {
        editor.putString(KEY_TODAY_WORKOUT, workout);
        editor.apply();
    }

    public int getWorkoutCompleted() {
        return prefs.getInt(KEY_WORKOUT_COMPLETED, 0);
    }

    public void setWorkoutCompleted(int completed) {
        editor.putInt(KEY_WORKOUT_COMPLETED, Math.min(completed, getWorkoutTotal()));
        editor.apply();
    }

    public void incrementWorkoutCompleted() {
        int current = getWorkoutCompleted();
        int total = getWorkoutTotal();

        if (current < total) {
            editor.putInt(KEY_WORKOUT_COMPLETED, current + 1);
            editor.apply();

            // If this completes all exercises for the day
            if (current + 1 == total) {
                updateStreakBasedOnLastWorkout();
                incrementDisciplineScore(5); // Bonus for completing all exercises
            } else {
                incrementDisciplineScore(2); // Small bonus for each exercise
            }

            // Add to workout history
            addWorkoutToHistory();
        }
    }

    public int getWorkoutTotal() {
        return prefs.getInt(KEY_WORKOUT_TOTAL, 6);
    }

    public void setWorkoutTotal(int total) {
        editor.putInt(KEY_WORKOUT_TOTAL, total);
        editor.apply();
    }

    public void resetWorkoutProgress() {
        editor.putInt(KEY_WORKOUT_COMPLETED, 0);
        editor.apply();
    }

    public int getWorkoutProgressPercentage() {
        int completed = getWorkoutCompleted();
        int total = getWorkoutTotal();
        return total > 0 ? (completed * 100 / total) : 0;
    }

    // ==================== WORKOUT HISTORY METHODS ====================

    public String getWorkoutHistory() {
        return prefs.getString(KEY_WORKOUT_HISTORY, "[]");
    }

    public void setWorkoutHistory(String history) {
        editor.putString(KEY_WORKOUT_HISTORY, history);
        editor.apply();
    }

    public void addWorkoutToHistory() {
        try {
            JSONArray history = new JSONArray(getWorkoutHistory());
            JSONObject workout = new JSONObject();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            workout.put("date", sdf.format(new Date()));
            workout.put("workout", getTodayWorkout());
            workout.put("completed", getWorkoutCompleted());
            workout.put("total", getWorkoutTotal());

            history.put(workout);

            // Keep only last 30 workouts
            if (history.length() > 30) {
                JSONArray newHistory = new JSONArray();
                for (int i = history.length() - 30; i < history.length(); i++) {
                    newHistory.put(history.get(i));
                }
                history = newHistory;
            }

            setWorkoutHistory(history.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ==================== WORKOUT LOG METHODS ====================

    public String getWorkoutLog() {
        return prefs.getString(KEY_WORKOUT_LOG, "[]");
    }

    public void saveWorkoutLog(JSONArray log) {
        editor.putString(KEY_WORKOUT_LOG, log.toString());
        editor.apply();
    }

    public void addWorkoutToLog(String exercise, int duration, String intensity, String date) {
        try {
            JSONArray log = new JSONArray(getWorkoutLog());
            JSONObject entry = new JSONObject();

            entry.put("exercise", exercise);
            entry.put("duration", duration);
            entry.put("intensity", intensity);
            entry.put("date", date);

            log.put(entry);
            saveWorkoutLog(log);

            // Update completion rate
            updateCompletionRate();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<JSONObject> getRecentWorkouts(int limit) {
        List<JSONObject> workouts = new ArrayList<>();
        try {
            JSONArray log = new JSONArray(getWorkoutLog());
            int start = Math.max(0, log.length() - limit);
            for (int i = start; i < log.length(); i++) {
                workouts.add(log.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return workouts;
    }

    // ==================== COMPLETION RATE METHODS ====================

    public float getCompletionRateLast2Weeks() {
        return prefs.getFloat(KEY_COMPLETION_RATE, 75.0f);
    }

    private void updateCompletionRate() {
        try {
            JSONArray log = new JSONArray(getWorkoutLog());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -14); // Last 14 days

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String cutoffDate = sdf.format(cal.getTime());

            int totalDays = 0;
            int completedDays = 0;

            // Count workouts in last 14 days
            for (int i = 0; i < log.length(); i++) {
                JSONObject entry = log.getJSONObject(i);
                String dateStr = entry.getString("date").substring(0, 10); // Get just date part

                if (dateStr.compareTo(cutoffDate) >= 0) {
                    totalDays++;
                    completedDays++;
                }
            }

            float rate = totalDays > 0 ? (completedDays * 100f / 14) : 75f; // Default 75%
            editor.putFloat(KEY_COMPLETION_RATE, Math.min(100, rate));
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ==================== GOAL ACHIEVEMENT METHODS ====================

    public float getGoalAchievementProgress() {
        return prefs.getFloat(KEY_GOAL_ACHIEVEMENT, 0.7f); // Default 70%
    }

    public void setGoalAchievementProgress(float progress) {
        editor.putFloat(KEY_GOAL_ACHIEVEMENT, Math.min(1.0f, Math.max(0f, progress)));
        editor.apply();
    }

    public void updateGoalAchievement() {
        float targetWeight = getTargetWeight();
        float currentWeight = 0f;
        try {
            currentWeight = Float.parseFloat(getUserWeight());
        } catch (NumberFormatException e) {
            currentWeight = 70f; // Default
        }

        float goal = getUserGoal().toLowerCase().contains("weight") ?
                calculateWeightGoalProgress(currentWeight, targetWeight) : 0.7f;

        setGoalAchievementProgress(goal);
        calculateAndUpdateDisciplineScore();
    }

    private float calculateWeightGoalProgress(float current, float target) {
        float prevWeight = getPrevMonthWeight();
        if (prevWeight == 0) return 0.7f;

        float totalChange = Math.abs(prevWeight - target);
        float currentChange = Math.abs(prevWeight - current);

        return totalChange > 0 ? Math.min(1.0f, currentChange / totalChange) : 0.7f;
    }

    private float getTargetWeight() {
        String goal = getUserGoal().toLowerCase();
        float current = 0f;
        try {
            current = Float.parseFloat(getUserWeight());
        } catch (NumberFormatException e) {
            current = 70f;
        }

        if (goal.contains("lose")) {
            return current * 0.9f; // Lose 10% of body weight
        } else if (goal.contains("build") || goal.contains("muscle")) {
            return current * 1.05f; // Gain 5%
        }
        return current; // Maintain
    }

    // ==================== ADAPTIVE PLAN METHODS ====================

    public int getMissedWorkoutsLast7Days() {
        try {
            JSONArray log = new JSONArray(getWorkoutLog());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -7);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String cutoffDate = sdf.format(cal.getTime());

            int workoutsInLastWeek = 0;
            for (int i = 0; i < log.length(); i++) {
                JSONObject entry = log.getJSONObject(i);
                String dateStr = entry.getString("date").substring(0, 10);
                if (dateStr.compareTo(cutoffDate) >= 0) {
                    workoutsInLastWeek++;
                }
            }

            // Expected workouts per week (based on activity level)
            int expectedWorkouts = getExpectedWorkoutsPerWeek();
            return Math.max(0, expectedWorkouts - workoutsInLastWeek);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getExpectedWorkoutsPerWeek() {
        String level = getUserActivityLevel().toLowerCase();
        if (level.contains("sedentary")) return 2;
        if (level.contains("lightly")) return 3;
        if (level.contains("moderate")) return 4;
        if (level.contains("very")) return 5;
        if (level.contains("super")) return 6;
        return 3; // Default
    }

    public String getAdaptivePlanMessage() {
        int missedWorkouts = getMissedWorkoutsLast7Days();
        float completionRate = getCompletionRateLast2Weeks();

        if (missedWorkouts >= 3) {
            return "🔄 Plan adapted: Intensity reduced by 20%";
        } else if (completionRate > 85) {
            return "⚡ Plan adapted: Intensity increased by 10%";
        }
        return "";
    }

    // ==================== PHOTO & NOTIFICATION METHODS ====================

    public boolean getNotificationPref(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public void setNotificationPref(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void savePhotoUri(String key, String uri) {
        editor.putString(key, uri);
        editor.apply();
    }

    public String getPhotoUri(String key) {
        return prefs.getString(key, "");
    }

    public void savePhotoDate(String key, String date) {
        editor.putString(key, date);
        editor.apply();
    }

    public String getPhotoDate(String key) {
        return prefs.getString(key, "Not uploaded");
    }

    // ==================== MONTHLY PROGRESS METHODS ====================

    public void savePrevMonthData(float prevWeight, float prevBmi) {
        editor.putFloat("prevMonthWeight", prevWeight);
        editor.putFloat("prevMonthBMI", prevBmi);
        editor.apply();
    }

    public float getPrevMonthWeight() {
        return prefs.getFloat("prevMonthWeight", 0f);
    }

    public float getPrevMonthBMI() {
        return prefs.getFloat("prevMonthBMI", 0f);
    }

    public float getWeightChange() {
        float current = 0f;
        try {
            current = Float.parseFloat(getUserWeight());
        } catch (NumberFormatException e) {
            current = 70f;
        }
        float previous = getPrevMonthWeight();
        return previous > 0 ? current - previous : 0;
    }

    public float getBMIChange() {
        float current = getBMI();
        float previous = getPrevMonthBMI();
        return previous > 0 ? current - previous : 0;
    }

    // ==================== HELPER METHODS ====================

    public void resetAllWorkoutData() {
        editor.putInt(KEY_WORKOUT_COMPLETED, 0);
        editor.putInt(KEY_WORKOUT_STREAK, 0);
        editor.putString(KEY_WORKOUT_HISTORY, "[]");
        editor.putString(KEY_WORKOUT_LOG, "[]");
        editor.putString(KEY_LAST_WORKOUT_DATE, "");
        editor.putFloat(KEY_COMPLETION_RATE, 75f);
        editor.apply();
    }

    public void printAllPrefs() {
        // Debug method to see all preferences
        android.util.Log.d("UserSessionManager", "All Preferences:");
        android.util.Log.d("UserSessionManager", "isLoggedIn: " + isLoggedIn());
        android.util.Log.d("UserSessionManager", "userName: " + getUserName());
        android.util.Log.d("UserSessionManager", "disciplineScore: " + getDisciplineScore());
        android.util.Log.d("UserSessionManager", "workoutStreak: " + getWorkoutStreak());
        android.util.Log.d("UserSessionManager", "completionRate: " + getCompletionRateLast2Weeks());
        android.util.Log.d("UserSessionManager", "goalAchievement: " + getGoalAchievementProgress());
    }
}