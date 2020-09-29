package com.learning.java.app.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import androidx.legacy.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.fragments.IntroFragment;
import com.learning.java.app.fragments.LoginFragment;
import com.learning.java.app.fragments.MainMenuFragment;
import com.learning.java.app.utilities.ServiceChecker;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {


    int MY_PERMISSIONS_REQUEST_ALL = 1;
    String[] Permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permissions
        if (!hasPermissions(this, Permissions)) {
            ActivityCompat.requestPermissions(this, Permissions, MY_PERMISSIONS_REQUEST_ALL);
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

    public int hasNavBar(Resources resources) {

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

    void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    void addFragment(Fragment fragment, String backStackFragmentName) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.main_container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.container);
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



}
