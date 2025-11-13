package com.learning.java.app.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;
import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IBitmapListener;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.ObjectListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;
import com.learning.java.app.services.AnalyticsHandler;
import com.learning.java.app.services.AuthHandler;
import com.learning.java.app.utilities.AESCrypt;
import com.learning.java.app.utilities.GlobalSingleton;

import java.net.URL;
import java.util.ArrayList;

public class LoginFragment extends BaseFragment {

    //views
    EditText emailEt, passwordEt;
    TextView registerTxtV;
    TextView forgotPassTxtV;
    TextView txtPrivacyPolicy;
    Button loginBtn;
    Button sendEmailBtn;
    Button backBtn;

    //variables from previous fragment
    public ArrayList<User> mainUserList;
    public ArrayList<Test> mainTestList;

    //variables
    User user;
    boolean isLogin = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_login, container, false);

        initialize();
        setOnClickViews();

        return baseView;
    }

    private void initialize() {
        emailEt = baseView.findViewById(R.id.login_email);
        passwordEt = baseView.findViewById(R.id.login_password);
        registerTxtV = baseView.findViewById(R.id.login_register);
        forgotPassTxtV = baseView.findViewById(R.id.login_forgot_pass);
        txtPrivacyPolicy = baseView.findViewById(R.id.login_privacy_policy);
        loginBtn = baseView.findViewById(R.id.login_login_btn);
        sendEmailBtn = baseView.findViewById(R.id.login_send_btn);
        backBtn = baseView.findViewById(R.id.login_back_btn);

        registerTxtV.setText(Html.fromHtml(getResources().getString(R.string.sing_up)));
        txtPrivacyPolicy.setText(Html.fromHtml(getString(R.string.login_privacy_policy)));

        //get all users
        if (mainUserList == null || mainUserList.isEmpty()) {
            getAllUsers();
        }

        //get all test
        if (mainTestList == null || mainTestList.isEmpty()) {
            getAllTest();
        }
    }

    private void getAllUsers() {
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


            }

            @Override
            public void getUserToken(String token) {

            }
        });
    }

    private void getAllTest() {
        FirestoreDatabase.getAllTests(new ITestListener() {
            @Override
            public void getAllTests(ArrayList<Test> testList) {
                mainTestList = testList;
            }

            @Override
            public void getTest(Test test) {

            }
        });
    }

    private void setOnClickViews() {
        // privacy policy
        txtPrivacyPolicy.setOnClickListener(v -> {
            String url = getString(R.string.privacy_policy_url);

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        //login
        loginBtn.setOnClickListener(v -> {

            if (emailEt.getText().toString().isEmpty() || passwordEt.getText().toString().isEmpty()) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.cell_alert_dialog);
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
                ((TextView) dialog.findViewById(R.id.txt)).setText(String.valueOf("You did not complete all the fields!"));
                dialog.show();
                return;
            }

            if (!GlobalSingleton.getInstance().isValidEmailAddress(emailEt.getText().toString())) {
                showSimpleAlert("Please provide a valid email!");
                return;
            }

            //show loading bar
            showLoadingBar();


            //sign in
            AuthHandler.signInUser(getActivity(), emailEt.getText().toString(), passwordEt.getText().toString(), new ObjectListener() {
                @Override
                public void getObject(Object obj) {
                    if (obj != null) {
                        try {
                            FirebaseUser receivedUser = (FirebaseUser) obj;
                            FirestoreDatabase.isUserExisting(new User(emailEt.getText().toString(), AESCrypt.encryptOrEmpty(passwordEt.getText().toString())), new IUserListener() {
                                @Override
                                public void getUser(final User user) {
                                    //hide loading bar
                                    hideLoadingBar();

                                    if (user != null) {
                                        //save token
                                        gs.setString(Constants.TOKEN_KEY, user.getToken());

                                        //user has email verified
                                        user.setEmailVerified(receivedUser.isEmailVerified() ? 1 : 0);
                                        if (user.getEmailVerified() == 0) {
                                            showSimpleAlert("Please confirm the email address. A verification email has been sent to you!");
                                            AuthHandler.sendVerificationEmail(getActivity(), null);
                                        }

                                        //send event
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Constants.EVENT_PARAM_EMAIL, user.getEmail());
                                        AnalyticsHandler.sendMessage(getActivity(), Constants.EVENT_LOGGED_IN, bundle);

                                        //send to next fragment
                                        MainMenuFragment fragmentMenu = new MainMenuFragment();
                                        fragmentMenu.user = user;
                                        fragmentMenu.userList = mainUserList;
                                        fragmentMenu.testList = mainTestList;
                                        replaceFragment(fragmentMenu, "MainMenuFragment");

                                    } else {
                                        showSimpleAlert("Email or password incorrect!");
                                    }
                                }

                                @Override
                                public void getAllUsers(ArrayList<User> userList) {

                                }

                                @Override
                                public void getUserToken(String token) {

                                }
                            });
                        } catch (Exception e) {
                            hideLoadingBar();
                            showSimpleAlert("Email or password incorrect!");
                            e.printStackTrace();
                        }
                    } else {
                        hideLoadingBar();
                        showSimpleAlert("Email or password incorrect!");
                    }
                }
            });


        });

        //login with facebook
        baseView.findViewById(R.id.login_facebook).setOnClickListener(v -> {
            showLoadingBar();
            ContainerActivity activity = (ContainerActivity) getActivity();
            activity.processFacebookLogin((id, name, email) -> {
                if (id == null || name == null || email == null) {
                    //hide loading bar
                    hideLoadingBar();
                    return;
                }

                //check if user exist in FireStore
                FirestoreDatabase.getUserBy("email", email, new IUserListener() {
                    @Override
                    public void getUser(final User user) {
//                                //hide loading bar
//                                hideLoadingBar();

                        if (user == null) {
                            //user does NOT exist in FireStore so we will add it
                            //create user
                            final User newUser = new User(mainUserList.size(), name, email, AESCrypt.encryptOrEmpty(Constants.FB_PASSWORD),
                                    "user", 1, 1, 0, 1, 0, 1, Constants.baseImage);
                            FirestoreDatabase.addUserWithCallback(newUser, false, new IRefreshListener() {
                                @Override
                                public void doRefresh(boolean doRefresh) {

                                    getFacebookProfilePicture(id, new IBitmapListener() {
                                        @Override
                                        public void getBitmap(Bitmap bitmap) {
                                            Bitmap facebookPicture = bitmap;
                                            if (facebookPicture == null) {
                                                facebookPicture = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.user);
                                            }

                                            FirestoreDatabase.addPhotoWithCallback(newUser, facebookPicture, new IRefreshListener() {
                                                @Override
                                                public void doRefresh(boolean doRefresh) {
                                                    //sort userList
                                                    mainUserList.add(newUser);
                                                    for (int i = 0; i < mainUserList.size() - 1; i++) {
                                                        if (mainUserList.get(i).getTotalPoints() < mainUserList.get(i + 1).getTotalPoints()
                                                                && !mainUserList.get(i).getFunction().equals(Constants.ADMIN_FUNCTION)
                                                                && !mainUserList.get(i + 1).getFunction().equals(Constants.ADMIN_FUNCTION)) {
                                                            User replacedUser = mainUserList.get(i);
                                                            mainUserList.set(i, mainUserList.get(i + 1));
                                                            mainUserList.set(i + 1, replacedUser);
                                                        }
                                                    }

                                                    for (int i = 1; i < mainUserList.size() - 1; i++) {
                                                        if (mainUserList.get(i).getId() != i && !mainUserList.get(i).getFunction().equals(Constants.ADMIN_FUNCTION)) {
                                                            mainUserList.get(i).setId(i);
                                                            final User sortUser = mainUserList.get(i);
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    FirestoreDatabase.updateUser(sortUser);
                                                                }
                                                            }, 0);
                                                        }
                                                    }

                                                    //hide loading bar
                                                    hideLoadingBar();

                                                    //save token
                                                    gs.setString(Constants.TOKEN_KEY, newUser.getToken());

                                                    //send event
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(Constants.EVENT_PARAM_EMAIL, newUser.getEmail());
                                                    AnalyticsHandler.sendMessage(getActivity(), Constants.EVENT_LOGGED_IN, bundle);

                                                    //send to next fragment
                                                    MainMenuFragment fragmentMenu = new MainMenuFragment();
                                                    fragmentMenu.user = newUser;
                                                    fragmentMenu.userList = mainUserList;
                                                    fragmentMenu.testList = mainTestList;
                                                    replaceFragment(fragmentMenu, "MainMenuFragment");
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        } else {
                            //hide loading bar
                            hideLoadingBar();

                            //save token
                            gs.setString(Constants.TOKEN_KEY, user.getToken());

                            //send event
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.EVENT_PARAM_EMAIL, user.getEmail());
                            AnalyticsHandler.sendMessage(getActivity(), Constants.EVENT_LOGGED_IN, bundle);

                            //send to next fragment
                            MainMenuFragment fragmentMenu = new MainMenuFragment();
                            fragmentMenu.user = user;
                            fragmentMenu.userList = mainUserList;
                            fragmentMenu.testList = mainTestList;
                            replaceFragment(fragmentMenu, "MainMenuFragment");
                        }


                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {

                    }

                    @Override
                    public void getUserToken(String token) {

                    }
                });
            });
        });

        //send email
        sendEmailBtn.setOnClickListener(view -> {
            if (emailEt.getText().toString().isEmpty()) {
                showSimpleAlert("You did not complete the email field!");
                return;
            }

            if (!GlobalSingleton.getInstance().isValidEmailAddress(emailEt.getText().toString())) {
                showSimpleAlert("Please provide a valid email!");
                return;
            }

            //how loading
            showLoadingBar();
            AuthHandler.sendPasswordResetEmail(getActivity(), emailEt.getText().toString(), new ObjectListener() {
                @Override
                public void getObject(Object obj) {
                    hideLoadingBar();
                    showSimpleAlert("If your address is registered, an email with instructions have been sent to you! If you didn't receive any email, please check the spam section.");
                    switchUI(true);
                }
            });


        });

        //back
        backBtn.setOnClickListener(view -> switchUI(true));

        //forgot password
        forgotPassTxtV.setOnClickListener(view -> switchUI(false));

        //register
        registerTxtV.setOnClickListener(v -> {
            RegisterFragment fragmentRegister = new RegisterFragment();
            fragmentRegister.mainUserList = mainUserList;
            fragmentRegister.mainTestList = mainTestList;
            replaceFragment(fragmentRegister, "RegisterFragment");
        });

    }

    public void getFacebookProfilePicture(String userID, final IBitmapListener bitmapListener) {

//        Bitmap bitmap = null;

        try {
            String imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large").toString();
//            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            ImageRequest request = new ImageRequest(imageURL, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    if (width > 800 || height > 800) {
                        int oldWidth = width; //i put this here because after above width change it's size, the height will be modified
                        width = gs.convertFromOneRangeToAnother(width, 0, width > height ? width : height, 0, 800);
                        height = gs.convertFromOneRangeToAnother(height, 0, oldWidth > height ? oldWidth : height, 0, 800);
                    }
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                    bitmapListener.getBitmap(bitmap);
                }
            }, 0, 0, null, null,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            bitmapListener.getBitmap(null);
                        }
                    });
            Volley.newRequestQueue(getActivity()).add(request);
        } catch (Exception e) {
            Log.e("RESPONSE", e.getMessage());
            e.printStackTrace();
            bitmapListener.getBitmap(null);
        }
    }

    private void switchUI(boolean value) {
        isLogin = value;
        if (isLogin) {
            passwordEt.setVisibility(View.VISIBLE);
            forgotPassTxtV.setVisibility(View.VISIBLE);
            registerTxtV.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);
            sendEmailBtn.setVisibility(View.GONE);
            backBtn.setVisibility(View.GONE);
        } else {
            passwordEt.setVisibility(View.GONE);
            forgotPassTxtV.setVisibility(View.GONE);
            registerTxtV.setVisibility(View.GONE);
            loginBtn.setVisibility(View.GONE);
            sendEmailBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.VISIBLE);
        }
    }

    public interface IFacebookListener {
        void getFacebookData(String id, String name, String email);
    }
}
