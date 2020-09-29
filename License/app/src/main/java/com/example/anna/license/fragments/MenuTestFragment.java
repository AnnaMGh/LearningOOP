package com.learning.java.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
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
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.ArrayList;

public class MenuTestFragment extends BaseFragment {

    LinearLayout testFooter;
    ArrayList<Test> standardQuestions = new ArrayList<>();
    ArrayList<Test> databaseQuestions = new ArrayList<>();
    ArrayList<Test> allQuestions = new ArrayList<>();
    Button addTestBtn, deleteTestBtn;
    ListView list;
    TestAdapter testAdapter;
    SqliteHelper database;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    User user;

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
        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    void initialize() {
        list = baseView.findViewById(R.id.menutest_list);
        testFooter = baseView.findViewById(R.id.menutest_footer);
        addTestBtn = baseView.findViewById(R.id.menutest_add_btn);
        deleteTestBtn = baseView.findViewById(R.id.menutest_remove_btn);

        database = SqliteHelper.getInstance(getActivity());
        settings = getActivity().getSharedPreferences("Learning_java", 0);
        editor = settings.edit();
        user = database.getUser(Integer.parseInt(settings.getString("ACCOUNT_KEY", null)));

        checkIfAdmin();
        setList();
    }

    void checkIfAdmin() {
        if (settings.getString("ACCOUNT_FUNCTION", null).equals("admin")) {
            testFooter.setVisibility(View.VISIBLE);
        }
    }

    void setList() {
        if (database.getAllTests().isEmpty()) {
            standardQuestions.add(new Test("Base level", false));
            standardQuestions.add(new Test("Level 1", false));
            standardQuestions.add(new Test("Level 2", false));
            standardQuestions.add(new Test("Level 3", false));
            standardQuestions.add(new Test("Level 4", false));

            for (Test t : standardQuestions) {
                database.addTest(t);
            }
        }

        allQuestions = (ArrayList<Test>) database.getAllTests();

        // allQuestions.addAll(standardQuestions);
        //allQuestions.addAll(databaseQuestions);

        testAdapter = new TestAdapter(getActivity(), R.layout.list_cell, R.id.list_text, allQuestions);
        list.setAdapter(testAdapter);
        testAdapter.notifyDataSetChanged();

    }


    void onClickViews() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectFragment(position);

            }
        });

        addTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFragment(new TestAddFragment(), "AddTestFragment", null);
            }
        });

        deleteTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new TestRemoveFragment(), "RemoveTestFragment", null);
            }
        });
    }

    void selectFragment(final int position) {
        Bundle bundle = new Bundle();

        switch (position) {
            case 0: {
                bundle.putInt("test", 1);
                Test1Fragment fragmentTest = new Test1Fragment();
                fragmentTest.listenerRefresh = new IRefreshListener() {
                    @Override
                    public void doRefresh() {
                        allQuestions.get(0).setChecked(true);
                        testAdapter.notifyDataSetChanged();
                    }
                };
                addFragment(fragmentTest, "Test1Fragment", bundle);

                break;
            }
            case 1: {
                if (1 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish the base level!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putInt("test", 2);
                Test2Fragment fragmentTest = new Test2Fragment();
                fragmentTest.listenerRefresh = new IRefreshListener() {
                    @Override
                    public void doRefresh() {
                        allQuestions.get(1).setChecked(true);
                        testAdapter.notifyDataSetChanged();
                    }
                };
                addFragment(fragmentTest, "Test2Fragment", bundle);
                break;
            }
            case 2: {
                if (2 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish level 1!", Toast.LENGTH_SHORT).show();
                    return;
                }

                bundle.putInt("test", 3);
                Test3Fragment fragmentTest = new Test3Fragment();
                fragmentTest.listenerRefresh = new IRefreshListener() {
                    @Override
                    public void doRefresh() {
                        allQuestions.get(2).setChecked(true);
                        testAdapter.notifyDataSetChanged();
                    }
                };
                addFragment(fragmentTest, "First you need to finish the base level 2!", bundle);
                break;
            }
            case 3: {
                if (3 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish level 3!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putInt("test", 4);
                Test4Fragment fragmentTest = new Test4Fragment();
                fragmentTest.listenerRefresh = new IRefreshListener() {
                    @Override
                    public void doRefresh() {
                        allQuestions.get(3).setChecked(true);
                        testAdapter.notifyDataSetChanged();
                    }
                };
                addFragment(fragmentTest, "Test4Fragment", bundle);
                break;
            }
            case 4: {
                if (4 > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish level 4!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putInt("test", 5);
                Test5Fragment fragmentTest = new Test5Fragment();
                fragmentTest.listenerRefresh = new IRefreshListener() {
                    @Override
                    public void doRefresh() {
                        allQuestions.get(4).setChecked(true);
                        testAdapter.notifyDataSetChanged();
                    }
                };
                addFragment(fragmentTest, "Test5Fragment", bundle);
                break;
            }
            default: {
                if (position > user.getTestProgress()) {
                    Toast.makeText(getActivity(), "First you need to finish level " + position + "!", Toast.LENGTH_SHORT).show();
                    return;
                }
                bundle.putInt("test", position + 1);
//                Toast.makeText(getActivity(), position + 1 + "", Toast.LENGTH_SHORT).show();
                TestNewFragment fragmentTest = new TestNewFragment();
                fragmentTest.listenerRefresh = new IRefreshListener() {
                    @Override
                    public void doRefresh() {
                        allQuestions.get(position).setChecked(true);
                        testAdapter.notifyDataSetChanged();
                    }
                };
                addFragment(fragmentTest, "TestNewFragment", bundle);
            }
        }
    }

    void addFragment(Fragment fragment, String backStackFragmentName, Bundle bundle) {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.setArguments(bundle);
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.add(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }


}
