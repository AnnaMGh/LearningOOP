package com.learning.java.app.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.learning.java.app.R;
import com.learning.java.app.adapters.QuestionAdapter;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.ICallbackListener;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.Question;
import com.learning.java.app.model.Test;

import java.util.ArrayList;

public class TestAddFragment extends BaseFragment {

    //views
    EditText title;
    Button button;
    ListView listViewQuestions;

    //variables
    ArrayList<Question> listQuestions = new ArrayList<>();
    QuestionAdapter adapterQuestions;

    //variables from previous fragment
    int testCountByNow;
    ITestListener listenerTest;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.add_test_fragment, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    void initialize() {

        //title:
        title = baseView.findViewById(R.id.addtest_title);

        //list questions
        listViewQuestions = baseView.findViewById(R.id.list_view_questions);
        listQuestions.add(new Question());
        adapterQuestions = new QuestionAdapter(getActivity(), R.layout.question_cell, R.id.edt_simple_question, listQuestions);
        adapterQuestions.listenerCallback = new ICallbackListener() {
            @Override
            public void onClickAdd() {
                listQuestions.add(new Question());
                adapterQuestions.notifyDataSetChanged();
            }

            @Override
            public void onClickRemove(int position) {
                listQuestions.remove(position);
                adapterQuestions.notifyDataSetChanged();
            }
        };
        listViewQuestions.setAdapter(adapterQuestions);

        //button add test
        button = baseView.findViewById(R.id.btn_add_test);

        //
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.requestFocus();
            }
        });
    }

    boolean validation() {

        for (Question question : listQuestions) {
            switch (question.type) {
                case "1": {
                    if (question.questions.get(0).isEmpty() || question.answers.get(0).isEmpty()) {
                        return false;
                    }
                    break;
                }
                case "2": {
                    if (question.questions.get(0).isEmpty() || question.answers.get(0).isEmpty()
                            || question.questions.get(1).isEmpty()) {
                        return false;
                    }
                    break;
                }

                case "3": {
                    if (question.questions.get(0).isEmpty() || question.answers.get(0).isEmpty()
                            || question.answers.get(1).isEmpty() || question.answers.get(2).isEmpty()
                            || question.multipleCorrectAnswerId.isEmpty() || question.multipleCorrectAnswerId.equals("0")) {
                        return false;
                    }
                    break;
                }
                default:
                    return false;
            }
        }
        return true;
    }

    void onClickViews() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.getText().toString().isEmpty()) {
                    AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                    builderAlert.setMessage("You did not write the title!")
                            .setCancelable(true);
                    builderAlert.show();
                    return;
                }

                if (!validation()) {

                    AlertDialog.Builder builderAlert = new AlertDialog.Builder(getActivity());
                    builderAlert.setMessage("You did not complete all the fields!")
                            .setCancelable(true);
                    builderAlert.show();
                    return;
                }

                //create test from question
                final Test newTest = new Test(title.getText().toString(), false, false, listQuestions);
                newTest.setId(testCountByNow + 1);

                //add test to Firestore
                FirestoreDatabase.addTest(newTest, new ITestListener() {
                    @Override
                    public void getAllTests(ArrayList<Test> testList) {

                    }

                    @Override
                    public void getTest(Test test) {
                        newTest.setToken(test.getToken());
                        if (listenerTest != null) {
                            listenerTest.getTest(newTest);
                            getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
                        } else {
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }


                    }
                });


                //refresh activity
//                getActivity().finish();
//                startActivity(getActivity().getIntent());


            }
        });
    }
}
