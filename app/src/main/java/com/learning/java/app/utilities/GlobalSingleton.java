package com.learning.java.app.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GlobalSingleton {

    private static final String TAG = "GLOBAL_SINGLETON";
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
            Log.d(TAG, "hasInternet: Missing context.");
            return false;
        }

        if (!hasInternetConnection(context)) return false;
        return canReachInternet();
    }

    /**
     * This return false if the phone is on camera on newer devices
     * */
    private boolean hasInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        Network network = cm.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities nc = cm.getNetworkCapabilities(network);
        if (nc == null) return false;

        // Are transport (WiFi / Cellular / Ethernet)?
        boolean hasTransport =
                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

        // Are internet functional?
        boolean hasInternetCapability =
                nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        return hasTransport && hasInternetCapability;
    }

    private static boolean canReachInternet() {
        try {
            HttpURLConnection urlc =
                    (HttpURLConnection) new URL("https://clients3.google.com/generate_204")
                            .openConnection();
            urlc.setRequestProperty("User-Agent", "Android");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 204);
        } catch (IOException e) {
            return false;
        }
    }


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
