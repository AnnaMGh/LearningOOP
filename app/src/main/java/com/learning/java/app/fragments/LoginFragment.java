package com.learning.java.app.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IBitmapListener;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;
import com.learning.java.app.services.AnalyticsHandler;

import java.net.URL;
import java.util.ArrayList;

public class LoginFragment extends BaseFragment {

    //views
    EditText emailEt, passwordEt;
    TextView registerTxtV;

    //variables from previous fragment
    public ArrayList<User> mainUserList;
    public ArrayList<Test> mainTestList;

    //variables
    User user;


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

        //get all users
        if (mainUserList == null || mainUserList.size() == 0) {
            getAllUsers();
        }

        //get all test
        if (mainTestList == null || mainTestList.size() == 0) {
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
                                && !mainUserList.get(j).getFunction().equals(Constants.ADMIN_NAME))
                                || mainUserList.get(j + 1).getFunction().equals(Constants.ADMIN_NAME)) {
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
        //login
        baseView.findViewById(R.id.login_login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                //show loading bar
                showLoadingBar();
                FirestoreDatabase.isUserExisting(new User(emailEt.getText().toString(), passwordEt.getText().toString()), new IUserListener() {
                    @Override
                    public void getUser(final User user) {
                        //hide loading bar
                        hideLoadingBar();

                        if (user != null) {
                            //save token
                            gs.setString(Constants.TOKEN_KEY, user.getToken());

                            //send event
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.EVENT_PARAM_EMAIL, user.getEmail());
                            AnalyticsHandler.sendMessage(getActivity(), Constants.EVENT_LOGGED_IN, bundle);

                            //send to next fragment
                            gs.setString(Constants.TOKEN_KEY, user.getToken());
                            MainMenuFragment fragmentMenu = new MainMenuFragment();
                            fragmentMenu.user = user;
                            fragmentMenu.userList = mainUserList;
                            fragmentMenu.testList = mainTestList;
                            replaceFragment(fragmentMenu, "MainMenuFragment");

                        } else {
                            Dialog dialog = new Dialog(getActivity());
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.cell_alert_dialog);
                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            }
                            ((TextView) dialog.findViewById(R.id.txt)).setText(String.valueOf("Email or password incorrect!"));
                            dialog.show();
                        }
                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {

                    }

                    @Override
                    public void getUserToken(String token) {

                    }
                });
            }
        });

        //login with facebook
        baseView.findViewById(R.id.login_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingBar();
                ContainerActivity activity = (ContainerActivity) getActivity();
                activity.processFacebookLogin(new IFacebookListener() {
                    @Override
                    public void getFacebookData(final String id, final String name, final String email) {
                        //check if user exist in FireStore
                        FirestoreDatabase.getUserBy("email", email, new IUserListener() {
                            @Override
                            public void getUser(final User user) {
//                                //hide loading bar
//                                hideLoadingBar();

                                if (user == null) {
                                    //user does NOT exist in FireStore so we will add it
                                    final User newUser = new User(mainUserList.size(), name, email, "",
                                            "user", 1, 1, 0, 1, 1, Constants.baseImage);
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
                                                                        && !mainUserList.get(i).getFunction().equals(Constants.ADMIN_NAME)
                                                                        && !mainUserList.get(i + 1).getFunction().equals(Constants.ADMIN_NAME)) {
                                                                    User replacedUser = mainUserList.get(i);
                                                                    mainUserList.set(i, mainUserList.get(i + 1));
                                                                    mainUserList.set(i + 1, replacedUser);
                                                                }
                                                            }

                                                            for (int i = 1; i < mainUserList.size() - 1; i++) {
                                                                if (mainUserList.get(i).getId() != i && !mainUserList.get(i).getFunction().equals(Constants.ADMIN_NAME)) {
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
                    }
                });
            }
        });

        //register
        registerTxtV.setText(Html.fromHtml(getResources().getString(R.string.sing_up)));
        registerTxtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment fragmentRegister = new RegisterFragment();
                fragmentRegister.mainUserList = mainUserList;
                fragmentRegister.mainTestList = mainTestList;
                replaceFragment(fragmentRegister, "RegisterFragment");
            }
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

    public interface IFacebookListener {
        void getFacebookData(String id, String name, String email);
    }
}
