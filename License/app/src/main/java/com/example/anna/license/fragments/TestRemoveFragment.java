package com.learning.java.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.learning.java.app.R;
import com.learning.java.app.adapters.TestAdapter;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.Test;

import java.util.ArrayList;

public class TestRemoveFragment extends BaseFragment {

    ListView listView;
    ArrayList<Test> tests = new ArrayList<>();
    SqliteHelper database;
    TestAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_remove_test, container, false);
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initialize();
        onClickViews();

        return baseView;
    }

    void initialize() {
        listView = baseView.findViewById(R.id.remove_test_list);
        database = SqliteHelper.getInstance(getActivity());
        tests = (ArrayList<Test>) database.getAllTests();
        adapter = new TestAdapter(getActivity(), R.layout.list_cell, R.id.list_text, tests);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void onClickViews() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int itemPosition = position;

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Do you really want to remove this test?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete all from database
                                for (Test t : tests) {
                                    database.deleteTest(t);
                                }

                                //delete clicked test from list
                                tests.remove(itemPosition);

                                //add the rest of test in database
                                for (Test t : tests) {
                                    database.addTest(t);
                                }

                                adapter.notifyDataSetChanged();


                                getActivity().finish();
                                startActivity(getActivity().getIntent());

                            }
                        })
                        .setNegativeButton("Back to Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setCancelable(true);

                dialog.show();

            }
        });
    }
}
