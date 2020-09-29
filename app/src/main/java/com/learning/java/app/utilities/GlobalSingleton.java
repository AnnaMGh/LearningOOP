package com.learning.java.app.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

public class GlobalSingleton {

    private static GlobalSingleton mInstance;
    public Context mContext;

    private SharedPreferences prefs;


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
}
