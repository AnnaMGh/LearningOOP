package com.learning.java.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.learning.java.app.R;
import com.learning.java.app.adapters.AccountsAdapter;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.User;

import java.util.ArrayList;

public class AdminAccountsFragment extends BaseFragment {

    ListView list;
    TextView noUsers;
    ArrayList<User> allUsers = new ArrayList<>();
    ArrayList<User> adminUsers = new ArrayList<>();
    AccountsAdapter adapter;
    SqliteHelper database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.admin_accounts_fragment, container, false);

        initialize();
        return baseView;
    }

    void initialize() {
        list = baseView.findViewById(R.id.adminaccount_list);
        noUsers = baseView.findViewById(R.id.adminaccount_nousers);

        database = SqliteHelper.getInstance(getActivity());
        allUsers = (ArrayList<User>) database.getAllUsers();

        for (User u : allUsers) {
            if (database.checkUserFunction(u.getName(), u.getPassword()).equals("admin")) {
                adminUsers.add(u);
            }
        }

        allUsers.removeAll(adminUsers);


        if(allUsers.isEmpty())
        {
            noUsers.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
        else
        {
            noUsers.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
        adapter = new AccountsAdapter(getActivity(), R.layout.list_cell_accounts, R.id.listcell_id, allUsers);
        adapter.listener = new AccountsAdapter.IListAccounts() {
            @Override
            public void getUser(User user) {
                database.deleteUser(user);
                allUsers.remove(user);
                adapter.notifyDataSetChanged();
            }
        };
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
