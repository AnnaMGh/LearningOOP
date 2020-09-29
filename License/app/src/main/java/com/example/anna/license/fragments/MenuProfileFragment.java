package com.learning.java.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;


public class MenuProfileFragment extends BaseFragment {

    WaveLoadingView learnLW, testLW;
    Switch notificationSw;
    Button logoutBtn, deleteAccountBtn, deleteAccountsBtn;
    TextView pointsTxtV;
    EditText usernameEdtv;
    SqliteHelper database;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    User user;
    int nrOfTests;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_menu_profile, container, false);

        initialize();
        setKeyboardDown();
        onClickViews();

        return baseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {

            if (user.getLearnProgress() == 1) {
                learnLW.setProgressValue(0);
                learnLW.setCenterTitle(String.valueOf(0) + "%");
            } else {
                int progress = user.getLearnProgress() * 100 / 30;
                if (progress > 100) {
                    progress = 100;
                }
                learnLW.setProgressValue(progress);
                learnLW.setCenterTitle(progress + "%");
            }

            if (user.getTestProgress() == 0) {
                testLW.setProgressValue(0);
                testLW.setCenterTitle(String.valueOf(0) + "%");
            } else {
                int progress = user.getTestProgress() * 100 / (nrOfTests);
                if (progress > 100) {
                    progress = 100;
                }
                testLW.setProgressValue(progress);
                testLW.setCenterTitle(progress + "%");
            }
        }
    }

    void initialize() {
        learnLW = baseView.findViewById(R.id.menuprofile_learn_wave);
        testLW = baseView.findViewById(R.id.menuprofile_test_wave);
        notificationSw = baseView.findViewById(R.id.menuprofile_swich);
        usernameEdtv = baseView.findViewById(R.id.menuprofile_name_edtv);
        pointsTxtV = baseView.findViewById(R.id.menuprofile_points_txtv);
        logoutBtn = baseView.findViewById(R.id.menuprofile_logout_btn);
        deleteAccountBtn = baseView.findViewById(R.id.menuprofile_delete_btn);
        deleteAccountsBtn = baseView.findViewById(R.id.menuprofile_deleteall_btn);

        settings = getActivity().getSharedPreferences("Learning_java", 0);
        editor = settings.edit();
        database = SqliteHelper.getInstance(getActivity());
        user = database.getUser(Integer.parseInt(settings.getString("ACCOUNT_KEY", null)));
        nrOfTests = database.getTestCount();


        if (user != null) {

            if (user.getLearnProgress() == 1) {
                learnLW.setProgressValue(0);
                learnLW.setCenterTitle(String.valueOf(0) + "%");
            } else {
                int progress = user.getLearnProgress() * 100 / 30;
                if (progress > 100) {
                    progress = 100;
                }
                learnLW.setProgressValue(progress);
                learnLW.setCenterTitle(progress + "%");
            }

            if (user.getTestProgress() == 0) {
                testLW.setProgressValue(0);
                testLW.setCenterTitle(String.valueOf(0) + "%");
            } else {
                int progress = user.getTestProgress() * 100 / (nrOfTests);
                if (progress > 100) {
                    progress = 100;
                }
                testLW.setProgressValue(progress);
                testLW.setCenterTitle(progress + "%");
            }

            pointsTxtV.setText(String.valueOf(user.getTotalPoints()));
            if (database.checkUserFunction(user.getName(), user.getPassword()).equals("admin")) {
                deleteAccountsBtn.setVisibility(View.VISIBLE);
            } else {
                deleteAccountsBtn.setVisibility(View.GONE);
            }

            if (user.getNotifications() == 1) {
                notificationSw.setChecked(true);
            } else {
                notificationSw.setChecked(false);
            }
        }
    }

    void setKeyboardDown() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void onClickViews() {
        notificationSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationSw.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    user.setNotifications(1);
                    database.updateUser(user);
                    Toast.makeText(getActivity(), "You turned on the notification!", Toast.LENGTH_SHORT).show();
                    //notification();

                } else {
                    notificationSw.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorGray), PorterDuff.Mode.SRC_IN);
                    user.setNotifications(0);
                    database.updateUser(user);
                    Toast.makeText(getActivity(), "You turned off the notification!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.apply();
                getActivity().finish();
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.apply();
                database.deleteUser(user);
                getActivity().finish();
            }
        });

        deleteAccountsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new AdminAccountsFragment(), "AdminAccountsFragment");
            }
        });
    }

    void addFragment(Fragment fragment, String backStackFragmentName) {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

}


