package com.learning.java.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.adapters.TestAdapter;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.ArrayList;

public class MenuTestFragment extends BaseFragment {

    //views
    LinearLayout testFooter;
    Button addTestBtn, deleteTestBtn;
    ListView list;

    //variables
    TestAdapter testAdapter;

    //variables from previous fragment
    public User user;
    public ArrayList<Test> testList;
    IRefreshListener listenerRefresh;
    ITestListener listenerTestRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_menu_test, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initialize() {
        list = baseView.findViewById(R.id.menutest_list);
        testFooter = baseView.findViewById(R.id.menutest_footer);
        addTestBtn = baseView.findViewById(R.id.menutest_add_btn);
        deleteTestBtn = baseView.findViewById(R.id.menutest_remove_btn);

        checkIfAdmin(user);
        setList();
    }

    void checkIfAdmin(User user) {
        if (user != null) {
            if (user.getFunction().equals("admin"))
                testFooter.setVisibility(View.VISIBLE);
        }
    }

    void setList() {
        if (testList == null) {
            FirestoreDatabase.getAllTests(new ITestListener() {
                @Override
                public void getAllTests(ArrayList<Test> tests) {
                    testList = tests;
                    setList();
                }

                @Override
                public void getTest(Test test) {
                }
            });

            return;
        }

        testAdapter = new TestAdapter(getActivity(), R.layout.list_cell, R.id.list_text, testList);
        testAdapter.setUser(user);
        list.setAdapter(testAdapter);
        testAdapter.notifyDataSetChanged();
    }

    void onClickViews() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFragment(testList.get(position));
            }
        });

        addTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestAddFragment fragmentAddTest = new TestAddFragment();
                fragmentAddTest.testCountByNow = testList.size();
                fragmentAddTest.listenerTest = new ITestListener() {
                    @Override
                    public void getAllTests(ArrayList<Test> receivedTestList) {

                    }

                    @Override
                    public void getTest(Test receivedTest) {
                        testList.add(receivedTest);
                        testAdapter.notifyDataSetChanged();
                    }
                };
                replaceFragment(fragmentAddTest, "AddTestFragment");
            }
        });

        deleteTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestRemoveFragment fragmentRemoveTest = new TestRemoveFragment();
                fragmentRemoveTest.testList = testList;
                fragmentRemoveTest.listenerTest = new ITestListener() {
                    @Override
                    public void getAllTests(ArrayList<Test> receivedTestList) {
                        testAdapter.notifyDataSetChanged();

                        if (listenerTestRefresh != null) {
                            listenerTestRefresh.getAllTests(receivedTestList);
                        }
                    }

                    @Override
                    public void getTest(Test test) {

                    }
                };
                replaceFragment(fragmentRemoveTest, "RemoveTestFragment");
            }
        });
    }

    void selectFragment(final Test clickedTest) {

        switch (clickedTest.getId()) {
            case 1: {
                Test1Fragment fragmentTest = new Test1Fragment();
                fragmentTest.test = clickedTest;
                fragmentTest.user = user;
                fragmentTest.listenerRefreshUser = new IUserListener() {
                    @Override
                    public void getUser(User updatedUser) {
                        user = updatedUser;
                        testAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {
                    }

                    @Override
                    public void getUserToken(String token) {
                    }
                };
                replaceFragment(fragmentTest, "Test1Fragment");

                break;
            }
            case 2: {
                if (1 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish " + testList.get(clickedTest.getId()-2).getTitle() + "!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Test2Fragment fragmentTest = new Test2Fragment();
                fragmentTest.test = clickedTest;
                fragmentTest.user = user;
                fragmentTest.listenerRefreshUser = new IUserListener() {
                    @Override
                    public void getUser(User updatedUser) {
                        user = updatedUser;
                        testAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {
                    }

                    @Override
                    public void getUserToken(String token) {
                    }
                };
                replaceFragment(fragmentTest, "Test2Fragment");
                break;
            }
            case 3: {
                if (2 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish " + testList.get(clickedTest.getId() - 2).getTitle() + "!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Test3Fragment fragmentTest = new Test3Fragment();
                fragmentTest.test = clickedTest;
                fragmentTest.user = user;
                fragmentTest.listenerRefreshUser = new IUserListener() {
                    @Override
                    public void getUser(User updatedUser) {
                        user = updatedUser;
                        testAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {
                    }

                    @Override
                    public void getUserToken(String token) {
                    }
                };
                replaceFragment(fragmentTest, "First you need to finish the base level 2!");
                break;
            }
            case 4: {
                if (3 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish " + testList.get(clickedTest.getId() - 2).getTitle() + "!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Test4Fragment fragmentTest = new Test4Fragment();
                fragmentTest.test = clickedTest;
                fragmentTest.user = user;
                fragmentTest.listenerRefreshUser = new IUserListener() {
                    @Override
                    public void getUser(User updatedUser) {
                        user = updatedUser;
                        testAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {
                    }

                    @Override
                    public void getUserToken(String token) {
                    }
                };
                replaceFragment(fragmentTest, "Test4Fragment");
                break;
            }
            case 5: {
                if (4 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish " + testList.get(clickedTest.getId() - 2).getTitle() + "!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Test5Fragment fragmentTest = new Test5Fragment();
                fragmentTest.test = clickedTest;
                fragmentTest.user = user;
                fragmentTest.listenerRefreshUser = new IUserListener() {
                    @Override
                    public void getUser(User updatedUser) {
                        user = updatedUser;
                        testAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {
                    }

                    @Override
                    public void getUserToken(String token) {
                    }
                };
                replaceFragment(fragmentTest, "Test5Fragment");
                break;
            }
            default: {
                if ((clickedTest.getId() - 1) > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish " + testList.get(clickedTest.getId() - 2).getTitle() + "!", Toast.LENGTH_SHORT).show();
                    return;
                }

                TestCustomFragment fragmentTest = new TestCustomFragment();
                fragmentTest.test = clickedTest;
                fragmentTest.user = user;
                fragmentTest.listenerRefreshUser = new IUserListener() {
                    @Override
                    public void getUser(User updatedUser) {

                        if (user != null && user.getName().equals("-1")) {
                            selectFragment(clickedTest);
                        } else {
                            user = updatedUser;
                            testAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void getAllUsers(ArrayList<User> userList) {
                    }

                    @Override
                    public void getUserToken(String token) {
                    }
                };
                replaceFragment(fragmentTest, "TestNewFragment");
            }
        }
    }


}
