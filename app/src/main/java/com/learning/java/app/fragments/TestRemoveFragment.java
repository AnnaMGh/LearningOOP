package com.learning.java.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.learning.java.app.R;
import com.learning.java.app.adapters.TestAdapter;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.ArrayList;

public class TestRemoveFragment extends BaseFragment {

    //views
    ListView listView;
    TextView btnDone;

    //variables from previous fragment
    User user;
    ArrayList<Test> testList;
    ITestListener listenerTest;

    //variables
    TestAdapter adapter;
    ArrayList<Test> updatedTestList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_remove_test, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    void initialize() {
        listView = baseView.findViewById(R.id.remove_test_list);
        btnDone = baseView.findViewById(R.id.txt_done);

        if (testList == null) {
            testList = new ArrayList<>();
        }
        adapter = new TestAdapter(getActivity(), R.layout.list_cell, R.id.list_text, testList);
        adapter.setUser(user);
        listView.setAdapter(adapter);
    }

    void onClickViews() {

        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (position < 5) {
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Do you really want to remove this test?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //remove item from database
                                FirestoreDatabase.deleteTest(testList.get(position));

                                //remove item from list
                                testList.remove(position);

                                //change rest of the test ids
                                if (position < testList.size()) {
                                    for (int i = position; i < testList.size(); i++) {
                                        Test testUpdated = testList.get(i);
                                        testUpdated.setId(i + 1);
                                        updatedTestList.add(testUpdated);
                                    }
                                }
                                adapter.notifyDataSetChanged();

                                //modify in Firestore
                                for (final Test updatedTest : updatedTestList) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            FirestoreDatabase.updateTest(updatedTest);
                                        }
                                    }, 0);
                                }

                                //refresh list from previous fragment
                                if (listenerTest != null) {
                                    listenerTest.getAllTests(testList);
                                }
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


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
            }
        });
    }
}
