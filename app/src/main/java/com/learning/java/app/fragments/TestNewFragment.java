package com.learning.java.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.learning.java.app.R;
import com.learning.java.app.database.SqliteHelper;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.HashMap;

public class TestNewFragment extends BaseFragment {

    TextView titleTxtV;
    TextView[] questions = new TextView[5];
    EditText[] answers = new EditText[5];
    RadioGroup[] radioGroup = new RadioGroup[5];
    RadioButton[] radio1Type = new RadioButton[3];
    RadioButton[] radio2Type = new RadioButton[3];
    RadioButton[] radio3Type = new RadioButton[3];
    RadioButton[] radio4Type = new RadioButton[3];
    RadioButton[] radio5Type = new RadioButton[3];
    TextView doneTxtV;

    Bundle bundle;
    SqliteHelper database;
    Test test;

    SharedPreferences settings;
    User user;

    int points;

    //previous fragment
    IRefreshListener listenerRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_add_test, container, false);
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initialize();
//        setQuestions();
        onClickViews();

        return baseView;
    }

    void initialize() {
        //title
        titleTxtV = baseView.findViewById(R.id.newtest_title);

        //questions
        questions[0] = baseView.findViewById(R.id.newtest_question1_txtv);
        questions[1] = baseView.findViewById(R.id.newtest_question2_txtv);
        questions[2] = baseView.findViewById(R.id.newtest_question3_txtv);
        questions[3] = baseView.findViewById(R.id.newtest_question4_txtv);
        questions[4] = baseView.findViewById(R.id.newtest_question5_txtv);

        //answer
        answers[0] = baseView.findViewById(R.id.newtest_question1_answer1);
        answers[1] = baseView.findViewById(R.id.newtest_question2_answer1);
        answers[2] = baseView.findViewById(R.id.newtest_question3_answer1);
        answers[3] = baseView.findViewById(R.id.newtest_question4_answer1);
        answers[4] = baseView.findViewById(R.id.newtest_question5_answer1);

        //radioGroup
        radioGroup[0] = baseView.findViewById(R.id.newtest_question1_answer_radiogroup);
        radioGroup[1] = baseView.findViewById(R.id.newtest_question2_answer_radiogroup);
        radioGroup[2] = baseView.findViewById(R.id.newtest_question3_answer_radiogroup);
        radioGroup[3] = baseView.findViewById(R.id.newtest_question4_answer_radiogroup);
        radioGroup[4] = baseView.findViewById(R.id.newtest_question5_answer_radiogroup);

        //radio1
        radio1Type[0] = baseView.findViewById(R.id.newtest_question1_rb_answer11);
        radio1Type[1] = baseView.findViewById(R.id.newtest_question1_rb_answer12);
        radio1Type[2] = baseView.findViewById(R.id.newtest_question1_rb_answer13);

        //radio2
        radio2Type[0] = baseView.findViewById(R.id.newtest_question2_rb_answer11);
        radio2Type[1] = baseView.findViewById(R.id.newtest_question2_rb_answer12);
        radio2Type[2] = baseView.findViewById(R.id.newtest_question2_rb_answer13);

        //radio3
        radio3Type[0] = baseView.findViewById(R.id.newtest_question3_rb_answer11);
        radio3Type[1] = baseView.findViewById(R.id.newtest_question3_rb_answer12);
        radio3Type[2] = baseView.findViewById(R.id.newtest_question3_rb_answer13);

        //radio4
        radio4Type[0] = baseView.findViewById(R.id.newtest_question4_rb_answer11);
        radio4Type[1] = baseView.findViewById(R.id.newtest_question4_rb_answer12);
        radio4Type[2] = baseView.findViewById(R.id.newtest_question4_rb_answer13);

        //radio5
        radio5Type[0] = baseView.findViewById(R.id.newtest_question5_rb_answer11);
        radio5Type[1] = baseView.findViewById(R.id.newtest_question5_rb_answer12);
        radio5Type[2] = baseView.findViewById(R.id.newtest_question5_rb_answer13);

        //button
        doneTxtV = baseView.findViewById(R.id.newtest_done_textview);


        bundle = getArguments();
        database = SqliteHelper.getInstance(getActivity());
//        test = database.getTest(bundle.getInt("test"));

        settings = getActivity().getSharedPreferences("Learning_java", 0);
        user = database.getUser(settings.getString("ACCOUNT_KEY", null));
    }

//    void setQuestions() {
//        //title
//        titleTxtV.setText(test.getTitle());
//
//        //questions
//        questions[0].setText(test.getQuestion1());
//        questions[1].setText(test.getQuestion2());
//        questions[2].setText(test.getQuestion3());
//        questions[3].setText(test.getQuestion4());
//        questions[4].setText(test.getQuestion5());
//
//        //type
//        setType();
//    }
//
//    void setType() {
//        if (test.getType1().equals("radio")) {
//            radioGroup[0].setVisibility(View.VISIBLE);
//            radio1Type[0].setText(test.getAnswer11());
//            radio1Type[1].setText(test.getAnswer12());
//            radio1Type[2].setText(test.getAnswer13());
//            answers[0].setVisibility(View.GONE);
//        } else {
//            radioGroup[0].setVisibility(View.GONE);
//            answers[0].setVisibility(View.VISIBLE);
//        }
//
//        if (test.getType2().equals("radio")) {
//            radioGroup[1].setVisibility(View.VISIBLE);
//            radio2Type[0].setText(test.getAnswer21());
//            radio2Type[1].setText(test.getAnswer22());
//            radio2Type[2].setText(test.getAnswer23());
//            answers[1].setVisibility(View.GONE);
//        } else {
//            radioGroup[1].setVisibility(View.GONE);
//            answers[1].setVisibility(View.VISIBLE);
//        }
//
//        if (test.getType3().equals("radio")) {
//            radioGroup[2].setVisibility(View.VISIBLE);
//            radio3Type[0].setText(test.getAnswer31());
//            radio3Type[1].setText(test.getAnswer32());
//            radio3Type[2].setText(test.getAnswer33());
//            answers[2].setVisibility(View.GONE);
//        } else {
//            radioGroup[2].setVisibility(View.GONE);
//            answers[2].setVisibility(View.VISIBLE);
//        }
//        if (test.getType4().equals("radio")) {
//            radioGroup[3].setVisibility(View.VISIBLE);
//            radio4Type[0].setText(test.getAnswer41());
//            radio4Type[1].setText(test.getAnswer42());
//            radio4Type[2].setText(test.getAnswer43());
//            answers[3].setVisibility(View.GONE);
//        } else {
//            radioGroup[3].setVisibility(View.GONE);
//            answers[3].setVisibility(View.VISIBLE);
//        }
//
//        if (test.getType5().equals("radio")) {
//            radioGroup[4].setVisibility(View.VISIBLE);
//            radio5Type[0].setText(test.getAnswer51());
//            radio5Type[1].setText(test.getAnswer52());
//            radio5Type[2].setText(test.getAnswer53());
//            answers[4].setVisibility(View.GONE);
//        } else {
//            radioGroup[4].setVisibility(View.GONE);
//            answers[4].setVisibility(View.VISIBLE);
//        }
//
//    }
//
//    boolean validateAnswers() {
//
//        points = 0;
//        RadioButton group;
//
//        //answer1
//        if (test.getType1().equals("radio")) {
//            if (radioGroup[0].getCheckedRadioButtonId() < 0) {
//                Toast.makeText(getActivity(), "You did not choose an answer for question 1", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            group = radioGroup[0].findViewById(radioGroup[0].getCheckedRadioButtonId());
//            if (group == null) {
//                return false;
//            }
//            if (group.getText().toString().equals(test.getAnswer1())) {
//                group.setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                group.setTextColor(getResources().getColor(R.color.colorRed));
//            }
//
//        } else {
//
//            if (answers[0].getText().toString().isEmpty()) {
//                Toast.makeText(getActivity(), "You did not answer question 1!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            if (answers[0].getText().toString().equals(test.getAnswer1())) {
//                answers[0].setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                answers[0].setTextColor(getResources().getColor(R.color.colorRed));
//            }
//        }
//
//
//        //answer2
//        if (test.getType2().equals("radio")) {
//            if (radioGroup[1].getCheckedRadioButtonId() < 0) {
//                Toast.makeText(getActivity(), "You did not choose an answer for question 2!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            group = radioGroup[1].findViewById(radioGroup[1].getCheckedRadioButtonId());
//            if (group == null) {
//                return false;
//            }
//            if (group.getText().toString().equals(test.getAnswer2())) {
//                group.setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                group.setTextColor(getResources().getColor(R.color.colorRed));
//            }
//        } else {
//            if (answers[1].getText().toString().isEmpty()) {
//                Toast.makeText(getActivity(), "You did not answer question 2!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if (answers[1].getText().toString().equals(test.getAnswer2())) {
//                answers[1].setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                answers[1].setTextColor(getResources().getColor(R.color.colorRed));
//            }
//        }
//
//        //answer3
//        if (test.getType3().equals("radio")) {
//            if (radioGroup[2].getCheckedRadioButtonId() < 0) {
//                Toast.makeText(getActivity(), "You did not choose an answer for question 3!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            group = radioGroup[2].findViewById(radioGroup[2].getCheckedRadioButtonId());
//            if (group == null) {
//                return false;
//            }
//            if (group.getText().toString().equals(test.getAnswer3())) {
//                group.setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                group.setTextColor(getResources().getColor(R.color.colorRed));
//            }
//
//        } else {
//            if (answers[2].getText().toString().isEmpty()) {
//                Toast.makeText(getActivity(), "You did not answer question 3!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if (answers[2].getText().toString().equals(test.getAnswer3())) {
//                answers[2].setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                answers[2].setTextColor(getResources().getColor(R.color.colorRed));
//            }
//        }
//
//
//        //answer4
//        if (test.getType4().equals("radio")) {
//            if (radioGroup[3].getCheckedRadioButtonId() < 0) {
//                Toast.makeText(getActivity(), "You did not choose an answer for question 4!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            group = radioGroup[3].findViewById(radioGroup[3].getCheckedRadioButtonId());
//            if (group == null) {
//                return false;
//            }
//            if (group.getText().toString().equals(test.getAnswer4())) {
//                group.setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                group.setTextColor(getResources().getColor(R.color.colorRed));
//            }
//
//        } else {
//            if (answers[3].getText().toString().isEmpty()) {
//                Toast.makeText(getActivity(), "You did not answer question 4!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if (answers[3].getText().toString().equals(test.getAnswer4())) {
//                answers[3].setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                answers[3].setTextColor(getResources().getColor(R.color.colorRed));
//            }
//        }
//
//        //answer5
//        if (test.getType5().equals("radio")) {
//            if (radioGroup[4].getCheckedRadioButtonId() < 0) {
//                Toast.makeText(getActivity(), "You did not choose an answer for question 5!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            group = radioGroup[4].findViewById(radioGroup[4].getCheckedRadioButtonId());
//            if (group == null) {
//                return false;
//            }
//            if (group.getText().toString().equals(test.getAnswer5())) {
//                group.setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                group.setTextColor(getResources().getColor(R.color.colorRed));
//            }
//        } else {
//            if (answers[4].getText().toString().isEmpty()) {
//                Toast.makeText(getActivity(), "You did not answer question 5!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if (answers[4].getText().toString().equals(test.getAnswer5())) {
//                answers[4].setTextColor(getResources().getColor(R.color.colorGreen));
//                points++;
//            } else {
//                answers[4].setTextColor(getResources().getColor(R.color.colorRed));
//            }
//        }
//
//        return true;
//    }

    void cleanViews() {
        for (int i = 0; i < 5; i++) {
            answers[i].setText("");
        }

        for (int i = 0; i < 3; i++) {
            radio1Type[i].setChecked(false);
            radio2Type[i].setChecked(false);
            radio3Type[i].setChecked(false);
            radio4Type[i].setChecked(false);
            radio5Type[i].setChecked(false);
        }
    }

    void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void onClickViews() {
        doneTxtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!validateAnswers()) {
//                    return;
//                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("You have " + points + "/5 correct answerds. Do you want to repeat the test?")
                        .setPositiveButton("Repeat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanViews();
                                closeKeyboard();
                                getFragmentManager().beginTransaction().detach(TestNewFragment.this).attach(TestNewFragment.this).commit();
                            }
                        })
                        .setNegativeButton("Back to Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeKeyboard();

                                if (points == 5) {
                                    if (!test.getChecked()) {
                                        user.setTestProgress(user.getTestProgress() + 1);
                                        user.setTotalPoints(user.getTotalPoints() + 1);

                                        //add test token in user's list of done tests
                                        HashMap<String, String> testsFinished = user.getTestsFinished();
                                        if (testsFinished == null) {
                                            testsFinished = new HashMap<>();
                                        }
                                        testsFinished.put(test.getToken(), "checked");
                                        user.setTestsFinished(testsFinished);

                                        database.updateUser(user);
                                        test.setChecked(true);
//                                        database.updateTest(test);
                                    }
                                }

                                if (listenerRefresh != null) {
//                                    listenerRefresh.doRefresh();
                                }
                                //getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
                                getActivity().finish();
                                startActivity(getActivity().getIntent());
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeKeyboard();
                                dialog.cancel();
                            }
                        })
                        .setCancelable(true);

                dialog.show();
            }
        });
    }
}
