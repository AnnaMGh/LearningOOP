package com.learning.java.app.fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
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

import java.util.ArrayList;

public class RegisterFragment extends BaseFragment {

    //views
    EditText usernameEt, emailEt, passwordEt, repeatPasswordEt;
    ImageView checkImgV1, checkImgV2;
    TextView checkTxtV1, checkTxtV2;

    //variables from previous fragment
    public ArrayList<User> mainUserList;
    public ArrayList<Test> mainTestList;

    //variables
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_register, container, false);

        initialize();
        setOnClickViews();

        return baseView;
    }

    private void initialize() {

        usernameEt = baseView.findViewById(R.id.register_username);
        emailEt = baseView.findViewById(R.id.register_email);
        passwordEt = baseView.findViewById(R.id.register_password);
        repeatPasswordEt = baseView.findViewById(R.id.register_password2);
        checkImgV1 = baseView.findViewById(R.id.register_check_imgv);
        checkImgV2 = baseView.findViewById(R.id.register_check_imgv2);
        checkTxtV1 = baseView.findViewById(R.id.register_check_txtv);
        checkTxtV2 = baseView.findViewById(R.id.register_check_txtv2);

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

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password1Validation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        repeatPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password2Validation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        baseView.findViewById(R.id.register_signup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if user complete all the fields correct
                if (usernameEt.getText().toString().isEmpty() || emailEt.getText().toString().isEmpty()
                        || passwordEt.getText().toString().isEmpty() || repeatPasswordEt.getText().toString().isEmpty()) {
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

                if (!password1Validation() || !password2Validation()) {
                    showSimpleAlert("Password don't matched!!");
                    return;
                }

                //create user
                if (usernameEt.getText().toString().equals(Constants.ADMIN_NAME)) {
                    user = new User(0, usernameEt.getText().toString(), emailEt.getText().toString(), passwordEt.getText().toString(),
                            Constants.ADMIN_FUNCTION, 1, 1, 0, 1, 0, 1, Constants.baseImage);
                } else {
                    user = new User(mainUserList.size(), usernameEt.getText().toString(), emailEt.getText().toString(), passwordEt.getText().toString(),
                            Constants.USER_FUNCTION, 1, 1, 0, 1, 0, 1, Constants.baseImage);
                }

                //show loading bar
                showLoadingBar();

                //register
                AuthHandler.signUpUser(getActivity(), user.getEmail(), user.getPassword(), new ObjectListener() {
                    @Override
                    public void getObject(Object obj) {
                        try {
                            FirebaseUser receivedUser = (FirebaseUser) obj;
                            AuthHandler.sendVerificationEmail(getActivity(), null);

                            if (receivedUser != null) {
                                //save in firebase with registration password encrypted
                                user.setPassword(AESCrypt.encryptOrEmpty(user.getPassword()));
                                FirestoreDatabase.addUserWithCallback(user, true, new IRefreshListener() {
                                    @Override
                                    public void doRefresh(boolean doRefresh) {
                                        //sort userList
                                        mainUserList.add(user);
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

                                        //send event
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Constants.EVENT_PARAM_EMAIL, user.getEmail());
                                        AnalyticsHandler.sendMessage(getActivity(), Constants.EVENT_REGISTERED, bundle);

                                        //send to next fragment
                                        MainMenuFragment fragmentMenu = new MainMenuFragment();
                                        fragmentMenu.user = user;
                                        fragmentMenu.userList = mainUserList;
                                        fragmentMenu.testList = mainTestList;
                                        replaceFragment(fragmentMenu, "MainMenuFragment");
                                    }
                                });
                            }
                        } catch (Exception e) {
                            //hide loading bar
                            hideLoadingBar();
                            try {
                                String exception = (String) obj;
                                if (exception.equalsIgnoreCase(AuthHandler.ERROR_EMAIL_ALREADY_IN_USE) ||
                                        exception.contains("FirebaseAuthUserCollisionException")) {
                                    //user already exist
                                    showSimpleAlert("This email address is already in use by another account.");
                                } else {
                                    //error
                                    showSimpleAlert("Something went wrong! Please try again later.");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showSimpleAlert("Something went wrong! Please try again later.");
                            }
                        }
                    }
                });


//                //check if this user already exist
//                FirestoreDatabase.getUserBy("email", user.getEmail(), new IUserListener() {
//                    @Override
//                    public void getUser(User userChecked) {
//
//                        //hide loading bar
//                        hideLoadingBar();
//
//                        if (userChecked != null) {
//                            //user already exist
//                            Dialog dialog = new Dialog(getActivity());
//                            dialog.setCancelable(true);
//                            dialog.setContentView(R.layout.cell_alert_dialog);
//                            if (dialog.getWindow() != null) {
//                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                            }
//                            ((TextView) dialog.findViewById(R.id.txt)).setText(String.valueOf("An user with this email already exist!"));
//                            dialog.show();
//                        } else {
//                            if (password1Validation() && password2Validation()) {
//                                FirestoreDatabase.addUserWithCallback(user, true, new IRefreshListener() {
//                                    @Override
//                                    public void doRefresh(boolean doRefresh) {
//                                        //sort userList
//                                        mainUserList.add(user);
//                                        for (int i = 0; i < mainUserList.size() - 1; i++) {
//                                            if (mainUserList.get(i).getTotalPoints() < mainUserList.get(i + 1).getTotalPoints()
//                                                    && !mainUserList.get(i).getFunction().equals(Constants.ADMIN_NAME)
//                                                    && !mainUserList.get(i + 1).getFunction().equals(Constants.ADMIN_NAME)) {
//                                                User replacedUser = mainUserList.get(i);
//                                                mainUserList.set(i, mainUserList.get(i + 1));
//                                                mainUserList.set(i + 1, replacedUser);
//                                            }
//                                        }
//
//                                        for (int i = 1; i < mainUserList.size() - 1; i++) {
//                                            if (mainUserList.get(i).getId() != i && !mainUserList.get(i).getFunction().equals(Constants.ADMIN_NAME)) {
//                                                mainUserList.get(i).setId(i);
//                                                final User sortUser = mainUserList.get(i);
//                                                new Handler().postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        FirestoreDatabase.updateUser(sortUser);
//                                                    }
//                                                }, 0);
//                                            }
//                                        }
//
//                                        //hide loading bar
//                                        hideLoadingBar();
//
//                                        //send event
//                                        Bundle bundle = new Bundle();
//                                        bundle.putString(Constants.EVENT_PARAM_EMAIL, user.getEmail());
//                                        AnalyticsHandler.sendMessage(getActivity(), Constants.EVENT_REGISTERED, bundle);
//
//                                        //send to next fragment
//                                        MainMenuFragment fragmentMenu = new MainMenuFragment();
//                                        fragmentMenu.user = user;
//                                        fragmentMenu.userList = mainUserList;
//                                        fragmentMenu.testList = mainTestList;
//                                        replaceFragment(fragmentMenu, "MainMenuFragment");
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void getAllUsers(ArrayList<User> userList) {
//                    }
//
//                    @Override
//                    public void getUserToken(String token) {
//
//                    }
//                });
            }
        });

    }


    boolean password1Validation() {

        if (hasNumbers(passwordEt.getText().toString().toCharArray()) && hasCapLetter(passwordEt.getText().toString().toCharArray())) {
            if (passwordEt.getText().length() > 7) {
                checkImgV1.setVisibility(View.VISIBLE);
                checkTxtV1.setVisibility(View.VISIBLE);
                checkImgV1.setImageResource(R.drawable.check_green);
                checkTxtV1.setTextColor(getResources().getColor(R.color.colorGreen));
                checkTxtV1.setText(String.valueOf("Strong password"));
            } else if (passwordEt.getText().length() > 5 && passwordEt.getText().length() < 7) {
                checkImgV1.setVisibility(View.VISIBLE);
                checkTxtV1.setVisibility(View.VISIBLE);
                checkImgV1.setImageResource(R.drawable.check_yellow);
                checkTxtV1.setTextColor(getResources().getColor(R.color.colorYellow));
                checkTxtV1.setText(String.valueOf("OK password"));
            } else {
                checkImgV1.setVisibility(View.VISIBLE);
                checkTxtV1.setVisibility(View.VISIBLE);
                checkImgV1.setImageResource(R.drawable.check_red);
                checkTxtV1.setTextColor(getResources().getColor(R.color.colorRed));
                checkTxtV1.setText(String.valueOf("Week password: Needs more than 4 characters."));
                return false;
            }
        } else {
            checkImgV1.setVisibility(View.VISIBLE);
            checkTxtV1.setVisibility(View.VISIBLE);
            checkImgV1.setImageResource(R.drawable.check_red);
            checkTxtV1.setTextColor(getResources().getColor(R.color.colorRed));
            checkTxtV1.setText(String.valueOf("Week password: Needs numbers and capitalized letters."));
            return false;
        }
        return true;
    }

    boolean password2Validation() {
        if (repeatPasswordEt.getText().toString().equals(passwordEt.getText().toString())) {
            checkImgV2.setVisibility(View.VISIBLE);
            checkTxtV2.setVisibility(View.GONE);
            checkImgV2.setImageResource(R.drawable.check_green);
            checkTxtV2.setTextColor(getResources().getColor(R.color.colorGreen));
            checkTxtV2.setText(String.valueOf("Passwords match."));
            return true;
        } else {
            checkImgV2.setVisibility(View.VISIBLE);
            checkTxtV2.setVisibility(View.VISIBLE);
            checkImgV2.setImageResource(R.drawable.check_red);
            checkTxtV2.setTextColor(getResources().getColor(R.color.colorRed));
            checkTxtV2.setText(String.valueOf("Passwords don't match."));
            return false;
        }
    }

    boolean hasNumbers(char[] s) {
        for (char c : s) {
            for (int i = 0; i < 10; i++) {
                if ((c + "").equals(i + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean hasCapLetter(char[] word) {
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z'};

        for (char character : word) {
            for (char letter : letters) {
                if ((character + "").equals(letter + "")) {
                    return true;
                }
            }
        }

        return false;
    }
}
