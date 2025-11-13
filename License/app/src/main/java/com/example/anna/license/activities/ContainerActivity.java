package com.learning.java.app.activities;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.learning.java.app.R;
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

public class  ContainerActivity extends AppCompatActivity {

    LoginFragment.IFacebookListener listener;
    public AccessToken accessToken;
    private CallbackManager mCallbackManager;
    public String facebookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_menu);

        keyHash();

        scheduleInFuture();

        if (accountVerification()) {
            addFragment(new MainMenuFragment(), "MainMenuFragment");

        } else {
            addFragment(new LoginFragment(), "LoginFragment");
        }
    }

    public void processFacebookLogin(LoginFragment.IFacebookListener iFacebookListener) {

        listener = iFacebookListener;

        mCallbackManager = CallbackManager.Factory.create();

        if (accessToken != null) {
            accessToken = com.facebook.AccessToken.getCurrentAccessToken();
            LoginManager.getInstance().logOut();
        }

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //check permission
                Log.d("facebook111", loginResult.getRecentlyGrantedPermissions() + "");
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String userDetail = response.getRawResponse();
                        try {
                            JSONObject json = new JSONObject(userDetail);

                            String id = "", email = "", name = "";

                            if (json.has("id")) {
                                id = json.getString("id");
                            }
                            if (json.has("email")) {
                                email = json.getString("email");
                            }
                            if (json.has("name")) {
                                name = json.getString("name");
                            }

                            facebookId = id;
                            if (listener != null) {
                                listener.getFacebookData(id, name, email);
                            }

                            Log.e("facebook111", id + " "
                                    + email + " "
                                    + name + " ");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("field", "name, email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("facebook111", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("facebook111", "onError " + error);
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(ContainerActivity.this,
                Arrays.asList("public_profile", "email"));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCallbackManager != null) {
            if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
    }

    void keyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.learning.java.app", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    boolean accountVerification() {
        SharedPreferences settings = getSharedPreferences("Learning_java", 0);
        String account = settings.getString("ACCOUNT_NAME", null);

        settings.edit().clear().apply();
        if (account != null) {
            return true;
        }

        return false;
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.container);
        if (fragment instanceof LoginFragment || fragment instanceof MainMenuFragment) {

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
        } else {
            super.onBackPressed();
        }
    }


    void addFragment(Fragment fragment, String backStackFragmentName) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

    public void scheduleInFuture() {
        long ct = System.currentTimeMillis();
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), ServiceChecker.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_IMMUTABLE);

        if (mgr != null) {
            //mgr.set(AlarmManager.RTC_WAKEUP, ct + 10 * 60 * 1000/*10 minute*/, pi);
            mgr.set(AlarmManager.RTC_WAKEUP, ct + 1 * 60 * 1000/*10 minute*/, pi);
        }
    }
}
