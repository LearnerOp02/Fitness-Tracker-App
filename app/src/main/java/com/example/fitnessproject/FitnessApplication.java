package com.example.fitnessproject;

import android.app.Application;
import android.content.Context;

public class FitnessApplication extends Application {
    private static FitnessApplication instance;
    private UserSessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            sessionManager = new UserSessionManager(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized FitnessApplication getInstance() {
        return instance;
    }

    public UserSessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new UserSessionManager(this);
        }
        return sessionManager;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}