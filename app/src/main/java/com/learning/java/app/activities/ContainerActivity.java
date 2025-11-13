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
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.storage.FirebaseStorage;
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
import com.learning.java.app.services.AnalyticsHandler;
import com.learning.java.app.services.AuthHandler;
import com.learning.java.app.utilities.GlobalSingleton;
import com.learning.java.app.utilities.ServiceChecker;
import com.learning.java.app.utilities.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ContainerActivity extends AppCompatActivity {

    private static final String TAG = "CONTAINER_ACTIVITY";
    //views
    public RelativeLayout layoutNetwork, layoutLoading;
    public LinearLayout llParentAd;
//    public AdView adView;

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

        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        setContentView(R.layout.activity_menu);

        Window window = getWindow();
        StatusBarUtil.makeStatusBarOpaque(window);
        StatusBarUtil.changeStatusBarColor(this, window);

        androidx.core.view.WindowInsetsControllerCompat controller =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false); // set true if your bar is light

        AnalyticsHandler.enableCrashlytics(ContainerActivity.this);
        AnalyticsHandler.registerAnalytics(ContainerActivity.this);
        AuthHandler.createAuth(ContainerActivity.this);

        //ad
        llParentAd = findViewById(R.id.ll_parent_ad);
//        adView = findViewById(R.id.ad_banner);
//        AdHandler.initialize(ContainerActivity.this, adView, llParentAd);

        //views
        layoutNetwork = findViewById(R.id.layout_network);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutLoading.setOnClickListener(view -> {
        });

        //firestore storage
        FirestoreDatabase.context = this;
        FirestoreDatabase.mStorageRef = FirebaseStorage.getInstance().getReference();

        //get info
        getInfoFromFirestore();

//        keyHash();
//        scheduleInFuture();

        //network
        layoutNetwork.setOnClickListener(view -> {
        });
        checkNetwork(doRefresh -> {
            showLayoutNetwork(true);
            if (!doRefresh) {
                if (isFinishing()) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ContainerActivity.this);
                builder.setMessage("Network connection was established. Do you want to reload you session?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (isFinishing()) {
                                System.exit(0);
                            } else {
                                Intent mStartActivity = new Intent(ContainerActivity.this, MainActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(ContainerActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager mgr = (AlarmManager) ContainerActivity.this.getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                System.exit(2);
                            }
                        })
                        .show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            /**
             * onBackPressed logic goes here - For instance:
             * Prevents closing the app to go home screen when in the
             * middle of entering data to a form
             * or from accidentally leaving a fragment with a WebView in it
             *
             * Unregistering the callback to stop intercepting the back gesture:
             * When the user transitions to the topmost screen (activity, fragment)
             * in the BackStack, unregister the callback by using
             * OnBackInvokeDispatcher.unregisterOnBackInvokedCallback
             * (https://developer.android.com/reference/kotlin/android/view/OnBackInvokedDispatcher#unregisteronbackinvokedcallback)
             */
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    this::handleBackPressed
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AuthHandler.startAuth(ContainerActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AnalyticsHandler.unregisterAnalytics();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {

            String permission = permissions[i];
            boolean result = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            Log.d(TAG, "onRequestPermissionsResult: " + requestCode + ", " + permission + ", " + result + ", " + listenerRefresh);

            if (Objects.equals(permission, Manifest.permission.CAMERA)) {
                // Camera accepted, go to main menu
                if (result && requestCode == Constants.MY_PERMISSIONS_REQUEST_CAMERA) {
                    if (listenerRefresh != null) {
                        listenerRefresh.doRefresh(true);
                    }
                }
            } else if (Objects.equals(permission, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Gallery accepted for SDK <33, go to main menu
                if (result && requestCode == Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                    if (listenerRefresh != null) {
                        listenerRefresh.doRefresh(true);
                    }
                }
            } else if (Objects.equals(permission, Manifest.permission.READ_MEDIA_IMAGES)) {
                // Gallery accepted for SDK >=33, go to main menu
                if (result && requestCode == Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                    if (listenerRefresh != null) {
                        listenerRefresh.doRefresh(true);
                    }
                }
            }
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
        Log.d(TAG, "onBackPressed");
        handleBackPressed();
    }

    private void handleBackPressed() {
        Log.d(TAG, "handleBackPressed");
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.container);
        if (fragment instanceof LoginFragment || fragment instanceof MainMenuFragment) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
        } else {
            manager.popBackStack();
        }
    }


    public void processFacebookLogin(LoginFragment.IFacebookListener iFacebookListener) {

        listener = iFacebookListener;

        mCallbackManager = CallbackManager.Factory.create();

        if (accessToken != null) {
            accessToken = com.facebook.AccessToken.getCurrentAccessToken();
            LoginManager.getInstance().logOut();
        }

        try {
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    //check permission
                    Log.d(TAG, "processFacebookLogin " + loginResult.getRecentlyGrantedPermissions());
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

                                Log.e(TAG, "processFacebookLogin " + id + " "
                                        + email + " "
                                        + name + " ");
                            } catch (JSONException e) {
                                Log.e(TAG, "processFacebookLogin: Error " + e);
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
                    if (listener != null) {
                        listener.getFacebookData(null, null, null);
                    }
                    Log.e(TAG, "processFacebookLogin - onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    if (listener != null) {
                        listener.getFacebookData(null, null, null);
                    }
                    Log.e(TAG, "processFacebookLogin - onError: " + error);
                }
            });
            LoginManager.getInstance().logInWithReadPermissions(ContainerActivity.this,
                    Arrays.asList("public_profile", "email"));
        } catch (Exception e) {
            Log.e(TAG, "processFacebookLogin: Error " + e);
            if (listener != null) {
                listener.getFacebookData(null, null, null);
            }
        }
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
                                && !mainUserList.get(j).getFunction().equals(Constants.ADMIN_FUNCTION))
                                || mainUserList.get(j + 1).getFunction().equals(Constants.ADMIN_FUNCTION)) {
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
                Log.e(TAG, "keyHash" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Toast.makeText(ContainerActivity.this, Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e(TAG, "keyHash: Error " + e);
        }
    }

    boolean accountVerification() {
        return !gs.getString(Constants.TOKEN_KEY).isEmpty();
    }

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
        return GlobalSingleton.getInstance().hasInternet(ContainerActivity.this);
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null
//                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null
//                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null
//        ) {
//            //we are connected to a network
//            return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
//        }
//        return false;
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
