package com.learning.java.app.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.learning.java.app.R;
import com.learning.java.app.fragments.IntroFragment;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("Exit me", false)) {
            finish();
        }

        final int navigationBarSize = hasNavBar(getResources());
        if (navigationBarSize > 0) {
            final LinearLayout navigationBarLayout = findViewById(R.id.main_navigationbar);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navigationBarLayout.getLayoutParams();
            params.height = navigationBarSize;

            navigationBarLayout.setLayoutParams(params);
            navigationBarLayout.setVisibility(View.VISIBLE);
        }
        makeStatusBarTransparent();

        addFragment(new IntroFragment(), "IntroFragment");
    }


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    private int hasNavBar(Resources resources) {

        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // Do whatever you need to do, this device has a navigation bar
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    private void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void addFragment(Fragment fragment, String backStackFragmentName) {

        if (isFinishing()) {
            return;
        }
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.main_container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

}
