package com.learning.java.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.adapters.AccountsAdapter;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.User;

import java.util.ArrayList;

public class AdminAccountsFragment extends BaseFragment {

    //views
    ListView list;
    TextView noUsers;

    //variables from previous fragment
    public ArrayList<User> userList;

    //variables
    ArrayList<User> standardUsersList = new ArrayList<>();
    ArrayList<User> updatedUserList = new ArrayList<>();
    AccountsAdapter adapter;
    int nrOfUserUpdatesAfterDelete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.admin_accounts_fragment, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    private void initialize() {

        //view
        list = baseView.findViewById(R.id.adminaccount_list);
        noUsers = baseView.findViewById(R.id.adminaccount_nousers);

        //get users
        if (userList == null || userList.size() == 0) {
            getAllUsers();
        } else {
            standardUsersList.clear();
            for (User user : userList) {
                if (!user.getFunction().equals(Constants.ADMIN_NAME)) {
                    standardUsersList.add(user);
                }
            }
        }

        //list adapter
        adapter = new AccountsAdapter(getActivity(), R.layout.list_cell_accounts, R.id.listcell_id, standardUsersList);
        adapter.listener = new AccountsAdapter.IListAccounts() {
            @Override
            public void onRemoveTapCallback(final int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Do you really want to remove this test?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //remove user from database
                                FirestoreDatabase.deleteUser(userList.get(position + 1));

                                //remove user's photo from firestore
                                FirestoreDatabase.deletePhoto(userList.get(position + 1));

                                //remove user from lists
                                userList.remove(position + 1);
                                standardUsersList.remove(position);

                                //check updatedUserList
                                if (updatedUserList == null) {
                                    updatedUserList = new ArrayList<>();
                                }
                                updatedUserList.clear();
                                nrOfUserUpdatesAfterDelete = 0;

                                //change rest of the user ids
                                if (position < standardUsersList.size()) {
                                    for (int index = position; index < standardUsersList.size(); index++) {

                                        //change userList also
                                        User userUpdated = userList.get(index + 1);
                                        userUpdated.setId(index + 1);

                                        //change standardUsersList
                                        standardUsersList.get(position).setId(position);

                                        updatedUserList.add(userUpdated);
                                    }
                                }
                                adapter.notifyDataSetChanged();


                                //modify in Firestore
                                for (final User userUpdated : updatedUserList) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            FirestoreDatabase.updateUserWithCallback(userUpdated, new IRefreshListener() {
                                                @Override
                                                public void doRefresh(boolean doRefresh) {
                                                    //refresh main activity after last update
                                                    if (++nrOfUserUpdatesAfterDelete == updatedUserList.size()) {
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
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setCancelable(true);

                dialog.show();
            }
        };
        list.setAdapter(adapter);
    }

    private void getAllUsers() {
        //get users
        FirestoreDatabase.getAllUsers(new IUserListener() {
            @Override
            public void getUser(User user) {
            }

            @Override
            public void getAllUsers(ArrayList<User> receivedUserList) {
                userList = receivedUserList;
                //sort users
                standardUsersList.clear();
                for (User u : userList) {
                    if (!u.getFunction().equals(Constants.ADMIN_NAME))
                        standardUsersList.add(u);
                }

                //show/hide list
                if (standardUsersList.isEmpty()) {
                    noUsers.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                } else {
                    noUsers.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }

                //list adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void getUserToken(String token) {
            }
        });
    }

    private void onClickViews() {
        //base view click blocked
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //done button
        baseView.findViewById(R.id.txt_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                if (i != null) {
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        });
    }
}
