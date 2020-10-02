package com.learning.java.app.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;

public class GlobalSingleton {

    private static GlobalSingleton mInstance;

    public Context mContext;

    private SharedPreferences prefs;
    public long lastShowedAd = 0;

    public static GlobalSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalSingleton();
        }
        return mInstance;
    }


    //Shared preferences
    private final static String SHARED_PREFERENCES_KEY = "Learning_java";

    public void setString(String name, String value) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            }
            prefs.edit().putString(name, value).apply();
        }
    }

    public String getString(String name) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            }
            return prefs.getString(name, "");
        }

        return "";
    }

    public void setInt(String name, int value) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            }
            prefs.edit().putInt(name, value).apply();
        }
    }

    public int getInt(String name) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            }
            return prefs.getInt(name, 0);
        }

        return 0;
    }

    public void setLong(String name, long value) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            }
            prefs.edit().putLong(name, value).apply();
        }
    }

    public long getLong(String name) {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            }
            return prefs.getLong(name, 0);
        }
        return 0;
    }


    public void cleanSharedPreferences() {
        if (mContext != null) {
            if (prefs == null) {
                prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            }
            prefs.edit().clear().apply();
        }
    }


    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public float dpToPx(float dp) {
        return (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public float pxToDp(float px) {
        return (px / Resources.getSystem().getDisplayMetrics().density);
    }

    //get window height
    public float getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    //get window width
    public float getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public int convertFromOneRangeToAnother(int oldValue, int oldMin, int oldMax, int newMin, int newMax) {
        return (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }

    public boolean hasInternet(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = (connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
