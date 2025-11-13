package com.learning.java.app;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.setClientToken(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(this);
        FirebaseApp.initializeApp(this);
    }
}
