package com.learning.java.app.services;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsHandler {

    private static FirebaseAnalytics analyticsInstance;

    public static void enableCrashlytics(Context context) {
        //this is automatically done with firebase
    }

    public static void forceCrash(Context context) {
        throw new RuntimeException("Test Crash"); // Force a crash
    }

    public static void registerAnalytics(Context context) {
        analyticsInstance = FirebaseAnalytics.getInstance(context);
    }

    public static void unregisterAnalytics() {
    }

    public static void sendMessage(Context context, String title, Bundle bundle) {
        if (analyticsInstance == null) {
            registerAnalytics(context);
        }
        analyticsInstance.logEvent(title, bundle);
    }
}
