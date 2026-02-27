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
        sessionManager = new UserSessionManager(this);
    }

    public static synchronized FitnessApplication getInstance() {
        return instance;
    }

    public UserSessionManager getSessionManager() {
        return sessionManager;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}