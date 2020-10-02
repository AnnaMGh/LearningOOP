package com.learning.java.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IBitmapListener;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.ObjectListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;
import com.learning.java.app.services.AuthHandler;

import java.util.ArrayList;
import java.util.HashMap;

import me.itangqi.waveloadingview.WaveLoadingView;


public class MenuProfileFragment extends BaseFragment {

    //views
    WaveLoadingView lWLearn, lWTest;
    Switch switchNotifications;
    Button btnLogout, btnDeleteAccount, btnDeleteAccountsList;
    ImageView[] imgTopUsers = new ImageView[3];

    //variables from previous fragment
    public User user;
    public ArrayList<User> userList;
    public ArrayList<Test> testList;
    ArrayList<User> updatedUserList = new ArrayList<>();
    public boolean canRefresh;

    //variables
    int nrOfTests = 1;
    int nrOfTestsLiveAndChecked = 1;
    int nrOfUserUpdatesAfterDelete;
    boolean fromRefresh;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_menu_profile, container, false);

        initialize();
        hideKeyboard();
        onClickViews();

        return baseView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshNotifications();
    }

    private void initialize() {
        lWLearn = baseView.findViewById(R.id.menuprofile_learn_wave);
        lWTest = baseView.findViewById(R.id.menuprofile_test_wave);
        switchNotifications = baseView.findViewById(R.id.menuprofile_swich);
        imgTopUsers[0] = baseView.findViewById(R.id.menuprofile_user1_img);
        imgTopUsers[1] = baseView.findViewById(R.id.menuprofile_user2_img);
        imgTopUsers[2] = baseView.findViewById(R.id.menuprofile_user3_img);
        btnLogout = baseView.findViewById(R.id.menuprofile_logout_btn);
        btnDeleteAccount = baseView.findViewById(R.id.menuprofile_delete_btn);
        btnDeleteAccountsList = baseView.findViewById(R.id.menuprofile_deleteall_btn);


        if (user != null) {
            //learn progress
            if (user.getLearnProgress() == 1) {
                lWLearn.setProgressValue(0);
                lWLearn.setCenterTitle(String.valueOf(0) + "%");
            } else {
                int progress = user.getLearnProgress() * 100 / 30;
                if (progress > 100) {
                    progress = 100;
                }
                lWLearn.setProgressValue(progress);
                lWLearn.setCenterTitle(progress + "%");
            }

            //test progress
            if (testList != null) {
                if (user.getTestsFinished() != null && user.getTestsFinished().size() > 0) {
                    HashMap<String, String> finishedTests = new HashMap<>(user.getTestsFinished());

                    boolean hadChanges = false;
                    for (String testFinishedName : finishedTests.keySet()) {
                        if (!testStillExist(testFinishedName)) {
                            hadChanges = true;
                            user.getTestsFinished().remove(testFinishedName);
                            user.setTestProgress(user.getTestProgress() - 1);
                            user.setTotalPoints(user.getTotalPoints() - 1);
                        }
                    }
                    if (hadChanges) {
                        FirestoreDatabase.updateUser(user);
                    }
                }

                nrOfTests = 0;
                nrOfTestsLiveAndChecked = 0;
                if (user.getFunction().equals(Constants.ADMIN_FUNCTION)) {
                    nrOfTests = testList.size();
                    nrOfTestsLiveAndChecked = user.getTestProgress();
                } else {
                    for (Test t : testList) {
                        if (t.isLive()) {
                            nrOfTests++;
                            if (user.getTestsFinished().containsKey(t.getToken())) {
                                nrOfTestsLiveAndChecked++;
                            }
                        }
                    }
                }
            }


            if (user.getTestProgress() == 0) {
                lWTest.setProgressValue(0);
                lWTest.setCenterTitle(String.valueOf(0) + "%");
            } else {
                int progress = nrOfTestsLiveAndChecked * 100 / (nrOfTests);
                if (progress > 100) {
                    progress = 100;
                }
                lWTest.setProgressValue(progress);
                lWTest.setCenterTitle(progress + "%");
            }

            //days & points
            ((TextView) baseView.findViewById(R.id.menuprofile_days_txtv)).setText(String.valueOf(user.getDaysInARaw()));
            ((TextView) baseView.findViewById(R.id.menuprofile_points_txtv)).setText(String.valueOf(user.getTotalPoints()));

            //notifications
            refreshNotifications();

            //check admin
            if (user.getFunction().equals(Constants.ADMIN_FUNCTION)) {
                btnDeleteAccountsList.setVisibility(View.VISIBLE);
            } else {
                btnDeleteAccountsList.setVisibility(View.GONE);
            }
        }

        getTop3Users();
    }

    private boolean testStillExist(String testName) {
        for (Test test : testList) {
            if (testName.equals(test.getToken())) {
                return true;
            }
        }
        return false;
    }

    private void refreshNotifications() {
        NotificationManagerCompat checkNotificationAvailability = NotificationManagerCompat.from(getActivity());
        checkNotificationAvailability.areNotificationsEnabled();

        //check if notification availability from database are the same with the ones from app
        if ((user.getNotifications() == 1 && !checkNotificationAvailability.areNotificationsEnabled()) || (user.getNotifications() == 0 && checkNotificationAvailability.areNotificationsEnabled())) {
            user.setNotifications(checkNotificationAvailability.areNotificationsEnabled() ? 1 : 0);
        }

        //change notification switch
        if (user.getNotifications() == 1) {
            switchNotifications.setChecked(true);
        } else {
            switchNotifications.setChecked(false);
        }

        fromRefresh = true;
    }

    private void handleNotifications() {
        // send to settings app notification
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

        //for Android 5-7
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            intent.putExtra("app_package", getActivity().getPackageName());
            intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
        }
        // for Android 8 and above
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());
        }

        startActivity(intent);
    }

    public void getTop3Users() {
        //top users
        if (userList != null) {
            int nrOfUsers = userList.size() - 1;//remove the admin from the nr of users
            if (nrOfUsers > 3) {
                nrOfUsers = 3;
            }
            for (int i = 0; i < nrOfUsers; i++) {
                final int finalI = i;
                FirestoreDatabase.getPhoto(userList.get(finalI + 1), new IBitmapListener() {
                    @Override
                    public void getBitmap(Bitmap bitmap) {
                        imgTopUsers[finalI].setVisibility(View.VISIBLE);
                        if (bitmap == null) {
                            imgTopUsers[finalI].setImageResource(R.drawable.user);
                        } else {
                            imgTopUsers[finalI].setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }
    }

    private void onClickViews() {
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

//                if (fromRefresh) {
//                    fromRefresh = false;
//                    return;
//                }

                if (isChecked) {
                    switchNotifications.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    if (user != null) {
                        user.setNotifications(1);
                        FirestoreDatabase.updateUser(user);
                    } else {
                        FirestoreDatabase.getUserBy(Constants.TOKEN_KEY, gs.getString(Constants.TOKEN_KEY), new IUserListener() {
                            @Override
                            public void getUser(User receivedUser) {
                                user = receivedUser;
                                user.setNotifications(1);
                                FirestoreDatabase.updateUser(user);
                            }

                            @Override
                            public void getAllUsers(ArrayList<User> userList) {

                            }

                            @Override
                            public void getUserToken(String token) {

                            }
                        });
                    }
                    handleNotifications();
                } else {
                    switchNotifications.getTrackDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorGray), PorterDuff.Mode.SRC_IN);
                    if (user != null) {
                        user.setNotifications(0);
                        FirestoreDatabase.updateUser(user);
                    } else {
                        FirestoreDatabase.getUserBy(Constants.TOKEN_KEY, gs.getString(Constants.TOKEN_KEY), new IUserListener() {
                            @Override
                            public void getUser(User receivedUser) {
                                user = receivedUser;
                                user.setNotifications(0);
                                FirestoreDatabase.updateUser(user);
                            }

                            @Override
                            public void getAllUsers(ArrayList<User> userList) {

                            }

                            @Override
                            public void getUserToken(String token) {

                            }
                        });
                    }
                    handleNotifications();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gs.cleanSharedPreferences();
                AuthHandler.singOut();

                Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                if (i != null) {
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Do you really want to remove this test?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //show loading bar
                                showLoadingBar();

                                //delete Auth account + sing out
                                AuthHandler.deleteAccount(new ObjectListener() {
                                    @Override
                                    public void getObject(Object obj) {
                                        try {
                                            boolean isSuccess = (boolean) obj;
                                            if (isSuccess) {
                                                AuthHandler.singOut();

                                                //clean shared preferences
                                                gs.cleanSharedPreferences();

                                                //remove item from database
                                                FirestoreDatabase.deleteUser(user);

                                                //remove photo from database
                                                FirestoreDatabase.deletePhoto(user);

                                                //remove item from list
                                                userList.remove(user.getId());

                                                //check updatedUserList
                                                if (updatedUserList == null) {
                                                    updatedUserList = new ArrayList<>();
                                                }
                                                updatedUserList.clear();
                                                nrOfUserUpdatesAfterDelete = 0;

                                                //change rest of the user ids
                                                if (user.getId() < userList.size()) {
                                                    for (int i = user.getId(); i < userList.size(); i++) {
                                                        User userUpdated = userList.get(i);
                                                        userUpdated.setId(i);
                                                        updatedUserList.add(userUpdated);
                                                    }
                                                }

                                                //modify in Firestore
                                                if (updatedUserList.size() == 0) {
                                                    hideLoadingBar();
                                                    Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                                                    if (i != null) {
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(i);
                                                    }
                                                } else {
                                                    for (final User updatedUser : updatedUserList) {
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                FirestoreDatabase.updateUserWithCallback(updatedUser, new IRefreshListener() {
                                                                    @Override
                                                                    public void doRefresh(boolean doRefresh) {
                                                                        //refresh main activity after last update
                                                                        if (++nrOfUserUpdatesAfterDelete == updatedUserList.size()) {
                                                                            hideLoadingBar();
                                                                            Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                                                                            if (i != null) {
                                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                startActivity(i);
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }, 0);
                                                    }
                                                }
                                            } else {
                                                hideLoadingBar();
                                                showSimpleAlert("Something went wrong. Please try again later!");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            hideLoadingBar();
                                            showSimpleAlert("Something went wrong. Please try again later!");
                                        }
                                    }
                                });


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(true);

                dialog.show();


            }
        });

        btnDeleteAccountsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminAccountsFragment fragmentAccounts = new AdminAccountsFragment();
                fragmentAccounts.userList = userList;
                replaceFragment(fragmentAccounts, "AdminAccountsFragment");
            }
        });

        View.OnClickListener listenerImageAction = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment fragmentUserProfile = new UserProfileFragment();
                fragmentUserProfile.user = userList.get(Integer.parseInt(view.getTag().toString()));
                fragmentUserProfile.bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                addFragment(fragmentUserProfile, "UserProfileFragment");
            }
        };

        imgTopUsers[0].setOnClickListener(listenerImageAction);
        imgTopUsers[1].setOnClickListener(listenerImageAction);
        imgTopUsers[2].setOnClickListener(listenerImageAction);
    }

}