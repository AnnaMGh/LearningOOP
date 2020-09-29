package com.learning.java.app.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.fragments.LoginFragment;
import com.learning.java.app.fragments.MainMenuFragment;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;
import com.learning.java.app.services.AdHandler;
import com.learning.java.app.services.AnalyticsHandler;
import com.learning.java.app.utilities.GlobalSingleton;
import com.learning.java.app.utilities.ServiceChecker;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ContainerActivity extends AppCompatActivity {

    //views
    public RelativeLayout layoutNetwork, layoutLoading;
    public LinearLayout llParentAd;
    public AdView adView;

    LoginFragment.IFacebookListener listener;
    public AccessToken accessToken;
    private CallbackManager mCallbackManager;
    public String facebookId;
    private boolean isNetworkBarShow;

    final private GlobalSingleton gs = GlobalSingleton.getInstance();

    private ArrayList<User> mainUserList = new ArrayList<>();
    private ArrayList<Test> mainTestList = new ArrayList<>();
    public IRefreshListener listenerRefresh;

    boolean isAllInfoDownload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //facebook
        // FacebookSdk.sdkInitialize(getApplicationContext());
        // AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_menu);

        AnalyticsHandler.enableCrashlytics(ContainerActivity.this);
        AnalyticsHandler.registerAnalytics(ContainerActivity.this);

        //ad
        llParentAd = findViewById(R.id.ll_parent_ad);
        adView = findViewById(R.id.ad_banner);
        AdHandler.initialize(ContainerActivity.this, adView, llParentAd);

        //views
        layoutNetwork = findViewById(R.id.layout_network);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //firestore storage
        FirestoreDatabase.context = this;
        FirestoreDatabase.mStorageRef = FirebaseStorage.getInstance().getReference();

        //get info
        getInfoFromFirestore();

//        keyHash();
//        scheduleInFuture();

        //network
        layoutNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        checkNetwork(new IRefreshListener() {
            @Override
            public void doRefresh(boolean doRefresh) {
                showLayoutNetwork(true);
                if (!doRefresh) {
                    if (isFinishing()) {
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(ContainerActivity.this);
                    builder.setMessage("Network connection was established. Do you want to reload you session?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (isFinishing()) {
                                        System.exit(0);
                                    } else {
                                        Intent mStartActivity = new Intent(ContainerActivity.this, MainActivity.class);
                                        int mPendingIntentId = 123456;
                                        PendingIntent mPendingIntent = PendingIntent.getActivity(ContainerActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager mgr = (AlarmManager) ContainerActivity.this.getSystemService(Context.ALARM_SERVICE);
                                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                        System.exit(2);
                                    }
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AnalyticsHandler.unregisterAnalytics();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean cameraAccepted = true;
        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.CAMERA: {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        cameraAccepted = false;
                    }
                    break;
                }
                case Manifest.permission.WRITE_EXTERNAL_STORAGE: {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        cameraAccepted = false;
                    }
                    break;
                }
                case Manifest.permission.READ_EXTERNAL_STORAGE: {
                    //gallery accepted, go to main menu
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED && requestCode == Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                        if (listenerRefresh != null) {
                            listenerRefresh.doRefresh(true);
                        }
                    }
                    break;
                }
            }
        }


        //camera accepted, go to main menu
        if (requestCode == Constants.MY_PERMISSIONS_REQUEST_CAMERA && cameraAccepted && listenerRefresh != null) {
            listenerRefresh.doRefresh(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (mCallbackManager != null) {
            if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {

        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.container);
        if (fragment instanceof LoginFragment || fragment instanceof MainMenuFragment) {

            if (doubleBackToExitPressedOnce) {
                //super.onBackPressed();
//                finish();
//                System.exit(0);

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
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


                            email = "facebook_" + id;

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
                parameters.putString("field", "id, first_name, last_name, name, email, picture");
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

    public void getInfoFromFirestore() {

        layoutLoading.setVisibility(View.VISIBLE);

        //get all users
        FirestoreDatabase.getAllUsers(new IUserListener() {
            @Override
            public void getUser(User user) {
            }

            @Override
            public void getAllUsers(ArrayList<User> userList) {

                mainUserList.addAll(userList);

                //sort userList
                for (int i = 0; i < mainUserList.size() - 1; i++) {
                    for (int j = 0; j < mainUserList.size() - 1; j++) {
                        if ((mainUserList.get(j).getTotalPoints() < mainUserList.get(j + 1).getTotalPoints()
                                && !mainUserList.get(j).getFunction().equals(Constants.ADMIN_NAME))
                                || mainUserList.get(j + 1).getFunction().equals(Constants.ADMIN_NAME)) {
                            User replacedUser = mainUserList.get(j);
                            mainUserList.set(j, mainUserList.get(j + 1));
                            mainUserList.set(j + 1, replacedUser);
                        }
                    }
                }

                //update to firestore
                for (int i = 0; i < mainUserList.size(); i++) {
                    if (mainUserList.get(i).getId() != i) {
                        mainUserList.get(i).setId(i);
                        final User sortedUser = mainUserList.get(i);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirestoreDatabase.updateUser(sortedUser);
                            }
                        }, 0);
                    }
                }

                if (isAllInfoDownload) {
                    checkUserState();
                }

                isAllInfoDownload = true;
            }

            @Override
            public void getUserToken(String token) {
            }
        });

        //get all test
        FirestoreDatabase.getAllTests(new ITestListener() {
            @Override
            public void getAllTests(ArrayList<Test> testList) {
                mainTestList = testList;

                if (isAllInfoDownload) {
                    checkUserState();
                }
                isAllInfoDownload = true;
            }

            @Override
            public void getTest(Test test) {

            }
        });


    }

    private void checkUserState() {
        //check if user logged
        if (accountVerification()) {
            FirestoreDatabase.getUserBy("token", gs.getString(Constants.TOKEN_KEY), new IUserListener() {
                @Override
                public void getUser(final User user) {

                    //hide loading bar
                    layoutLoading.setVisibility(View.GONE);

                    if (user != null) {
                        // send the user & tests and go to menu
                        MainMenuFragment fragmentMenu = new MainMenuFragment();
                        fragmentMenu.user = user;
                        fragmentMenu.userList = mainUserList;
                        fragmentMenu.testList = mainTestList;
                        addFragment(fragmentMenu, "MainMenuFragment");
                    } else {
                        //go to LoginFragment
                        LoginFragment loginFragment = new LoginFragment();
                        loginFragment.mainUserList = mainUserList;
                        loginFragment.mainTestList = mainTestList;
                        addFragment(loginFragment, "LoginFragment");
                    }
                }

                @Override
                public void getAllUsers(ArrayList<User> receivedUserList) {
                }

                @Override
                public void getUserToken(String token) {
                }
            });
        } else {
            //hide login fragment
            layoutLoading.setVisibility(View.GONE);

            //go to LoginFragment
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.mainUserList = mainUserList;
            loginFragment.mainTestList = mainTestList;
            addFragment(loginFragment, "LoginFragment");
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
        return !gs.getString(Constants.TOKEN_KEY).equals("");
    }

    boolean doubleBackToExitPressedOnce = false;

    void showLayoutNetwork(boolean toShow) {
        if (toShow) {
            layoutNetwork.setVisibility(View.VISIBLE);

            ObjectAnimator obj = ObjectAnimator.ofFloat(layoutNetwork, "y", 0);
            obj.setDuration(500);
            obj.start();

        } else {
            layoutNetwork.setY(-50);
            layoutNetwork.setVisibility(View.GONE);
        }
    }

    void checkNetwork(final IRefreshListener refreshListener) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        if (!isNetworkAvailable() && !isNetworkBarShow) {
                            refreshListener.doRefresh(true);
                            isNetworkBarShow = true;
                        } else if (isNetworkAvailable() && isNetworkBarShow) {
                            refreshListener.doRefresh(false);
                            isNetworkBarShow = false;
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                return true;
            }
        }
        return false;
    }

    public void scheduleInFuture() {
        long ct = System.currentTimeMillis();
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), ServiceChecker.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);

        if (mgr != null) {
            //mgr.set(AlarmManager.RTC_WAKEUP, ct + 10 * 60 * 1000/*10 minute*/, pi);
            mgr.set(AlarmManager.RTC_WAKEUP, ct + 1 * 60 * 1000/*10 minute*/, pi);
        }
    }

    void addFragment(Fragment fragment, String backStackFragmentName) {
        if (this.isFinishing()) {
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
//        transaction.commit();

        try {
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
        }

    }
}
