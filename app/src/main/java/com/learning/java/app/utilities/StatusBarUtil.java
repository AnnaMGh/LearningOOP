package com.learning.java.app.utilities;

import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.learning.java.app.R;

public class StatusBarUtil {

    /**
     * Should be called in onCreate method
     */
    public static void makeStatusBarTransparent(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * Should be called in onCreate method
     */
    public static void makeStatusBarOpaque(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * Should be called in onCreate method
     */
    public static void changeStatusBarColor(Context context, Window window) {
        window.setStatusBarColor(
                androidx.core.content.ContextCompat.getColor(context, R.color.colorPrimaryDark) // your color
        );
    }
}
