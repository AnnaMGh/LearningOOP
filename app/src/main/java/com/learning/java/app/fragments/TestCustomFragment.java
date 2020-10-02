package com.learning.java.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.Question;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class TestCustomFragment extends BaseFragment {

    //views
    TextView titleTextView, doneTextView;
    LinearLayout testContainer;

    //variables
    int points = 0;
    String[] textModified;
    boolean[] toModifyText;
    int[] checkedEdt;

    //variables from previous fragment
    IUserListener listenerRefreshUser;
    User user;
    Test test;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.test_custom_fragment, container, false);

        initialize();
        setQuestions();
        onClickViews();

        return baseView;
    }

    private void initialize() {

        titleTextView = baseView.findViewById(R.id.test_title);
        doneTextView = baseView.findViewById(R.id.test_done_textview);
        testContainer = baseView.findViewById(R.id.test_container);

        titleTextView.setText(test.getTitle().toUpperCase());
    }

    void setQuestions() {

        textModified = new String[test.getQuestionsList().size()];
        toModifyText = new boolean[test.getQuestionsList().size()];
        checkedEdt = new int[test.getQuestionsList().size()];

        for (int i = 0; i < test.getQuestionsList().size(); i++) {
            checkedEdt[i] = -1;
        }

        testContainer.removeAllViews();

        for (Question question : test.getQuestionsList()) {

            //add question layout
            switch (question.type) {
                case "1":
                    testContainer.addView(getType1Layout(question));
                    break;
                case "2":
                    testContainer.addView(getType2Layout(question, test.getQuestionsList().indexOf(question)));
                    break;
                case "3":
                    testContainer.addView(getType3Layout(question));
                    break;
                default:
            }
        }
    }

    private LinearLayout getType1Layout(Question question) {
        //question parent
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(0));

        LinearLayout questionLayout = new LinearLayout(getActivity());
        questionLayout.setTag("question_layout_" + (testContainer.getChildCount() + 1));
        questionLayout.setLayoutParams(params);
        questionLayout.setBackgroundResource(R.drawable.rounded_blue);
        questionLayout.setGravity(Gravity.CENTER);
        questionLayout.setOrientation(LinearLayout.VERTICAL);
        questionLayout.setPadding((int) gs.dpToPx(10), (int) gs.dpToPx(10), (int) gs.dpToPx(10), (int) gs.dpToPx(10));

        //create question
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView questionTxt = new TextView(getActivity());
        questionTxt.setTag("question_question_" + (testContainer.getChildCount() + 1));
        questionTxt.setLayoutParams(params1);
        questionTxt.setText(question.questions.get(0));
        questionTxt.setTextColor(getResources().getColor(R.color.colorWhite));
        questionTxt.setTextSize(15);

        //create answer
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params2.setMargins((int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(15));

        final EditText answerEdt = new EditText(getActivity());
        answerEdt.setTag("question_answer_" + (testContainer.getChildCount() + 1));
        answerEdt.setLayoutParams(params2);
        answerEdt.setLines(1);
        answerEdt.setMaxWidth((int) gs.dpToPx(question.answers.get(0).length() * 100f));
        answerEdt.setMinWidth((int) gs.dpToPx(question.answers.get(0).length() * 10f));
        answerEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(question.answers.get(0).length())});
        answerEdt.setGravity(Gravity.CENTER);
        answerEdt.setTextSize(15);
        answerEdt.setTextColor(getResources().getColor(R.color.colorAccent));
        answerEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerEdt.requestFocus();
            }
        });

        answerEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int color = getResources().getColor(R.color.colorAccent);
                if (!b) {
                    color = getResources().getColor(R.color.colorDarkGray);
                }

                answerEdt.setTextColor(color);
                ColorStateList colorStateList = ColorStateList.valueOf(color);
                ViewCompat.setBackgroundTintList(answerEdt, colorStateList);
            }
        });

        //add the views
        questionLayout.addView(questionTxt);
        questionLayout.addView(answerEdt);

        return questionLayout;
    }

    private LinearLayout getType2Layout(final Question question, final int index) {

        //question parent
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(0));

        LinearLayout questionLayout = new LinearLayout(getActivity());
        questionLayout.setTag("question_layout_" + (testContainer.getChildCount() + 1));
        questionLayout.setLayoutParams(params);
        questionLayout.setBackgroundResource(R.drawable.rounded_blue);
        questionLayout.setGravity(Gravity.CENTER_VERTICAL);
//        questionLayout.setOrientation(LinearLayout.HORIZONTAL);
        questionLayout.setPadding((int) gs.dpToPx(10), (int) gs.dpToPx(10), (int) gs.dpToPx(10), (int) gs.dpToPx(10));


        //create question
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText answerEdt = new EditText(getActivity());
        answerEdt.setTag("question_answer_" + (testContainer.getChildCount() + 1));
        answerEdt.setLayoutParams(params1);
        answerEdt.setTextColor(getResources().getColor(R.color.colorWhite));
//        answerEdt.setMaxWidth((int) gs.dpToPx(question.answers.get(0).length() * 9f));
//        answerEdt.setMinWidth((int) gs.dpToPx(question.answers.get(0).length() * 7f));
        answerEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(question.questions.get(0).length()
                + 1
                + question.answers.get(0).length()
                + 1
                + question.questions.get(1).length()
        )});
        answerEdt.setGravity(Gravity.CENTER_VERTICAL);
        answerEdt.setText(Html.fromHtml("<font color=\"#ffffff\">" + question.questions.get(0) + " </font>" + "&ensp;"
                + "<font color=\"#111111\"><a href=\"\" style=\"text-decoration: none; border-bottom: 1px solid #111111; color:#111111;\" >" + addBlankSpaces(question.answers.get(0).length() - 1) + "</a></font>"
                + "&ensp;" + question.questions.get(1)));
        answerEdt.setTextSize(15);
        answerEdt.setBackgroundResource(0);

        final int rightBound = question.questions.get(0).length() + 1;
        final int leftBound = question.questions.get(0).length() + 1 + question.answers.get(0).length();
        textModified[index] = addBlankSpaces(question.answers.get(0).length());

        answerEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (answerEdt.getSelectionStart() < rightBound || answerEdt.getSelectionStart() > (rightBound + textModified[index].length()) || answerEdt.getSelectionEnd() > (rightBound + textModified[index].length())) {
                        answerEdt.setSelection(rightBound);
                    }

                    if (textModified[index].equals(addBlankSpaces(question.answers.get(0).length()))) {
                        textModified[index] = "";
                    }

                    answerEdt.setText(Html.fromHtml("<font color=\"#ffffff\">" + question.questions.get(0) + "</font>" + "&nbsp;"
                            + "<font color=\"#00a0ee\"><a href=\"\" style=\"text-decoration: none; border-bottom: 1px solid #111111; color:#00a0ee;\" >" + textModified[index] + "</a></font>"
                            + "&nbsp;" + question.questions.get(1)));

                } else {
                    if (textModified[index] == null || textModified[index].isEmpty()) {
                        textModified[index] = addBlankSpaces(question.answers.get(0).length());
                    }
                    answerEdt.setText(Html.fromHtml("<font color=\"#ffffff\">" + question.questions.get(0) + "</font>" + "&nbsp;"
                            + "<font color=\"#333333\"><a href=\"\" style=\"text-decoration: none; border-bottom: 1px solid #333333; color:#333333;\">" + textModified[index] + "</a></font>"
                            + "&nbsp;" + question.questions.get(1)));

                    answerEdt.clearFocus();
                }
            }
        });

        answerEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getId() != answerEdt.getId()) {
                    answerEdt.requestFocus();
                }

                if (answerEdt.getSelectionStart() < rightBound || answerEdt.getSelectionStart() > leftBound || answerEdt.getSelectionEnd() > leftBound) {
                    answerEdt.setSelection(rightBound);
                }
            }
        });

        answerEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

                if (answerEdt.getSelectionStart() == 0) {
                    try {
                        answerEdt.setSelection(rightBound + textModified[index].length());
                    } catch (Exception e) {
                        Log.e("crash", "" + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (answerEdt.getSelectionStart() < rightBound || answerEdt.getSelectionStart() > (rightBound + textModified[index].length() + 1)) {
                    try {
                        answerEdt.setSelection(rightBound);
                    } catch (Exception e) {
                        Log.e("crash", "" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    try {
//                        int position = answerEdt.getText().toString().indexOf(question.questions.get(1)) - rightBound;
//                        if (position < 0) {
//                            textModified[index] = charSequence.toString().substring(rightBound, rightBound + 1);
//                        } else {
//                            textModified[index] = charSequence.toString().substring(rightBound, answerEdt.getText().toString().indexOf(question.questions.get(1)) - 1);
//                        }

                        textModified[index] = charSequence.toString().substring(rightBound, answerEdt.getText().toString().lastIndexOf(question.questions.get(1)) - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String color;
                if (!(getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getId() == answerEdt.getId())) {
                    color = "333333";
                } else {
                    color = "00a0ee";
                }
                if (checkedEdt[index] == 1) {
                    color = "009900";
                } else if (checkedEdt[index] == 0) {
                    color = "990000";
                }

                Spanned modifiedText = Html.fromHtml("<font color=\"#ffffff\">" + question.questions.get(0) + "</font>" + "&nbsp;"
                        + "<font color=\"#" + color + "\"><a href=\"\" style=\"text-decoration: none; border-bottom: 1px solid #" + color + "; color:#" + color + ";\" >" + textModified[index] + "</a></font>"
                        + "&nbsp;" + question.questions.get(1));

                if (!editable.toString().equals(modifiedText.toString()) || toModifyText[index]) {
                    toModifyText[index] = false;
                    answerEdt.setText(modifiedText);
                }

                if (!editable.toString().startsWith(question.questions.get(0)) || !editable.toString().endsWith(question.questions.get(1))) {
                    answerEdt.setSelection(rightBound);
                }
                toModifyText[index] = true;
            }
        });


        questionLayout.addView(answerEdt);

        return questionLayout;
    }

    private LinearLayout getType3Layout(Question question) {
        //question parent
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(15), (int) gs.dpToPx(0));

        LinearLayout questionLayout = new LinearLayout(getActivity());
        questionLayout.setTag("question_layout_" + (testContainer.getChildCount() + 1));
        questionLayout.setLayoutParams(params);
        questionLayout.setBackgroundResource(R.drawable.rounded_blue);
        questionLayout.setGravity(Gravity.CENTER);
        questionLayout.setOrientation(LinearLayout.VERTICAL);
        questionLayout.setPadding((int) gs.dpToPx(10), (int) gs.dpToPx(10), (int) gs.dpToPx(10), (int) gs.dpToPx(10));

        //create question
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView questionTxt = new TextView(getActivity());
        questionTxt.setTag("question_question_" + (testContainer.getChildCount() + 1));
        questionTxt.setLayoutParams(params1);
        questionTxt.setText(question.questions.get(0));
        questionTxt.setTextColor(getResources().getColor(R.color.colorWhite));
        questionTxt.setTextSize(15);

        //create answer group
        RadioGroup answerRdGr = new RadioGroup(getActivity());
        answerRdGr.setTag("question_answer_" + (testContainer.getChildCount() + 1));
        answerRdGr.setLayoutParams(params1);
        answerRdGr.setGravity(Gravity.CENTER_VERTICAL);

        //create answer no. 1
        final RadioButton answerRdBtn1 = new RadioButton(getActivity());
        answerRdBtn1.setLayoutParams(params1);
        answerRdBtn1.setGravity(Gravity.CENTER_VERTICAL);
        answerRdBtn1.setText(question.answers.get(0));
        answerRdBtn1.setTextColor(getResources().getColor(R.color.colorWhite));
        answerRdBtn1.setTextSize(15);

        //create answer no.2
        final RadioButton answerRdBtn2 = new RadioButton(getActivity());
        answerRdBtn2.setLayoutParams(params1);
        answerRdBtn2.setGravity(Gravity.CENTER_VERTICAL);
        answerRdBtn2.setText(question.answers.get(1));
        answerRdBtn2.setTextColor(getResources().getColor(R.color.colorWhite));
        answerRdBtn2.setTextSize(15);

        //create answer no. 1
        final RadioButton answerRdBtn3 = new RadioButton(getActivity());
        answerRdBtn3.setLayoutParams(params1);
        answerRdBtn3.setGravity(Gravity.CENTER_VERTICAL);
        answerRdBtn3.setText(question.answers.get(2));
        answerRdBtn3.setTextColor(getResources().getColor(R.color.colorWhite));
        answerRdBtn3.setTextSize(15);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerRdBtn1.setTextColor(getResources().getColor(R.color.colorWhite));
                answerRdBtn2.setTextColor(getResources().getColor(R.color.colorWhite));
                answerRdBtn3.setTextColor(getResources().getColor(R.color.colorWhite));

                ((RadioButton) view).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        };

        answerRdBtn1.setOnClickListener(onClickListener);
        answerRdBtn2.setOnClickListener(onClickListener);
        answerRdBtn3.setOnClickListener(onClickListener);


        //add the views
        answerRdGr.addView(answerRdBtn1);
        answerRdGr.addView(answerRdBtn2);
        answerRdGr.addView(answerRdBtn3);

        questionLayout.addView(questionTxt);
        questionLayout.addView(answerRdGr);

        return questionLayout;
    }

    private String addBlankSpaces(int nrOfSpaces) {
        StringBuilder spaces = new StringBuilder("");

        for (int i = 0; i < nrOfSpaces; i++) {
            spaces.append("&nbsp;");
        }

        return spaces.toString();
    }

    private boolean validateAnswer() {

        points = 0;

        ArrayList<AnswerView> answerViewList = getAnswers();

        for (AnswerView answerView : answerViewList) {
            if (answerView.status == -1) {
                Toast.makeText(getActivity(), "You did not answered all the questions!", Toast.LENGTH_LONG).show();
                return false;
            } else {
                points += answerView.status;
            }
        }

        for (int i = 0; i < answerViewList.size(); i++) {

            switch (answerViewList.get(i).type) {
                case 1: {
                    int color = getResources().getColor(R.color.colorRed);
                    if (answerViewList.get(i).status == 1) {
                        color = getResources().getColor(R.color.colorGreen);
                    }

                    ((EditText) answerViewList.get(i).view).setTextColor(color);
                    ColorStateList colorStateList = ColorStateList.valueOf(color);
                    ViewCompat.setBackgroundTintList((answerViewList.get(i).view), colorStateList);

                    break;
                }
                case 2: {
                    String color = "009900";
                    if ((checkedEdt[i] = answerViewList.get(i).status) == 1) {
                        color = "990000";
                    }
                    ((EditText) answerViewList.get(i).view).setText(Html.fromHtml("<font color=\"#ffffff\">" + test.getQuestionsList().get(answerViewList.get(i).index).questions.get(0) + "</font>" + "&nbsp;"
                            + "<font color=\"#" + color + "\"><a href=\"\" style=\"text-decoration: none; border-bottom: 1px solid #" + color + "; color:#" + color + ";\" >" + textModified[answerViewList.get(i).index] + "</a></font>"
                            + "&nbsp;" + test.getQuestionsList().get(answerViewList.get(i).index).questions.get(1)));

                    break;
                }
                case 3: {
                    int color = getResources().getColor(R.color.colorRed);
                    if (answerViewList.get(i).status == 1) {
                        color = getResources().getColor(R.color.colorGreen);
                    }

                    RadioGroup rdgr = (RadioGroup) answerViewList.get(i).view;
                    RadioButton rdbtn = rdgr.findViewById(rdgr.getCheckedRadioButtonId());
                    if (rdbtn != null) {
                        rdbtn.setTextColor(color);
                    }
                }
            }
        }


        for (int i = 0; i < test.getQuestionsList().size(); i++) {
            checkedEdt[i] = -1;
        }

        Toast.makeText(getActivity(), "You have correct answered for " + points + " questions.", Toast.LENGTH_SHORT).show();

        return true;
    }

    private ArrayList<AnswerView> getAnswers() {

        ArrayList<AnswerView> answerViewsList = new ArrayList<>();
        LinearLayout layout;
        Question question;

        for (int i = 0; i < testContainer.getChildCount(); i++) {
            try {

                layout = (LinearLayout) testContainer.getChildAt(i);
                question = test.getQuestionsList().get(i);

                AnswerView answerView = null;
                switch (question.type) {
                    case "1":
                        answerView = validateType1Answer(layout, i);
                        break;
                    case "2":
                        answerView = validateType2Answer(layout, i);
                        break;
                    case "3":
                        answerView = validateType3Answer(layout, i);
                }

                if (answerView != null) {
                    answerViewsList.add(answerView);
                }

            } catch (Exception e) {
                Log.e("Crash", "TestCustomFragment | getAnswers | " + e.getMessage());
                e.printStackTrace();
            }
        }

        return answerViewsList;
    }

    private AnswerView validateType1Answer(LinearLayout layout, int index) {
        View child;
        AnswerView answerView = new AnswerView();

        for (int i = 0; i < layout.getChildCount(); i++) {
            child = layout.getChildAt(i);
            if (child.getTag().equals(String.valueOf("question_answer_" + (index + 1)))) {
                try {
                    String answer = ((EditText) child).getText().toString();
                    int status = 0;
                    //correct
                    if (answer.equalsIgnoreCase(test.getQuestionsList().get(index).answers.get(0))) {
                        status = 1;
                    }
                    //not completed
                    else if (answer.isEmpty()) {
                        status = -1;
                    }

                    //clean view
                    ((EditText) child).setTextColor(getResources().getColor(R.color.colorWhite));

                    answerView.index = index;
                    answerView.view = child;
                    answerView.status = status;
                    answerView.type = 1;

                } catch (Exception e) {
                    Log.e("Crash", "TestCustomFragment | validateType1Answer | " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return answerView;
    }

    private AnswerView validateType2Answer(LinearLayout layout, int index) {
        View child;
        AnswerView answerView = new AnswerView();

        for (int i = 0; i < layout.getChildCount(); i++) {
            child = layout.getChildAt(i);
            if (child.getTag().equals(String.valueOf("question_answer_" + (index + 1)))) {
                try {
                    String answer = ((EditText) child).getText().toString();

                    Question question = test.getQuestionsList().get(index);

//                    int a = question.questions.get(0).length() + 1;
//                    int b = ((EditText) child).getText().toString().lastIndexOf(question.questions.get(1));
//
//                    int position = question.questions.get(0).length() - ((EditText) child).getText().toString().indexOf(question.questions.get(1));
//                    if (position < 0) {
//                        answer = answer.substring(question.questions.get(0).length() + 1, question.questions.get(0).length() + 2);
//                    } else {
                    answer = answer.substring(question.questions.get(0).length() + 1, ((EditText) child).getText().toString().lastIndexOf(question.questions.get(1)) - 1);


                    int status = 0;


                    //correct
                    if (answer.equalsIgnoreCase(question.answers.get(0))) {
                        status = 1;
                    }
                    //not completed
                    else if (answer.isEmpty() || (answer.replaceAll("\\s+", "")).isEmpty()) {
                        status = -1;
                    }

                    //clean view
                    Spanned modifiedText = Html.fromHtml("<font color=\"#ffffff\">" + test.getQuestionsList().get(index).questions.get(0) + "</font>" + "&nbsp;"
                            + "<font color=\"#444444\"><a href=\"\" style=\"text-decoration: none; border-bottom: 1px solid #444444; color:#444444;\" >" + textModified[index] + "</a></font>"
                            + "&nbsp;" + test.getQuestionsList().get(index).questions.get(1));
                    ((EditText) child).setText(modifiedText);

                    answerView.index = index;
                    answerView.view = child;
                    answerView.status = status;
                    answerView.type = 2;

                } catch (Exception e) {
                    Log.e("Crash", "TestCustomFragment | validateType2Answer | " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return answerView;
    }

    private AnswerView validateType3Answer(LinearLayout layout, int index) {
        View child;
        AnswerView answerView = new AnswerView();
        int correctAnswer = Integer.parseInt(test.getQuestionsList().get(index).multipleCorrectAnswerId);

        for (int i = 0; i < layout.getChildCount(); i++) {
            child = layout.getChildAt(i);
            if (child.getTag().equals(String.valueOf("question_answer_" + (index + 1)))) {
                try {
                    int answer = ((RadioGroup) child).getCheckedRadioButtonId();
                    if (answer > 3) {
                        answer = answer % 3;
                        if (answer == 0) {
                            answer = 3;
                        }
                    }
                    int status = 0;

                    //correct
                    if (answer == correctAnswer) {
                        RadioButton rdbn = ((RadioGroup) child).findViewById(((RadioGroup) child).getCheckedRadioButtonId());
                        rdbn.setTextColor(getResources().getColor(R.color.colorGreen));
                        status = 1;
                    }
                    //not completed
                    else if (answer < 0) {
                        status = -1;
                    }

                    //clean view
                    for (int j = 0; j < ((RadioGroup) child).getChildCount(); j++) {
                        ((RadioButton) ((RadioGroup) child).getChildAt(j)).setTextColor(getResources().getColor(R.color.colorWhite));
                    }


                    answerView.index = index;
                    answerView.view = child;
                    answerView.status = status;
                    answerView.type = 3;

                } catch (Exception e) {
                    Log.e("Crash", "TestCustomFragment | validateType3Answer | " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return answerView;
    }

    private void cleanViews() {
        ArrayList<AnswerView> answerViewList = getAnswers();

        for (int i = 0; i < answerViewList.size(); i++) {

            switch (answerViewList.get(i).type) {
                case 1: {
                    ((EditText) answerViewList.get(i).view).setText(Html.fromHtml(addBlankSpaces(test.getQuestionsList().get(answerViewList.get(i).index).answers.get(0).length())));
                    break;
                }
                case 2: {
                    String color = "444444";
                    textModified[answerViewList.get(i).index] = addBlankSpaces(test.getQuestionsList().get(answerViewList.get(i).index).answers.get(0).length());
                    ((EditText) answerViewList.get(i).view).setText(Html.fromHtml("<font color=\"#ffffff\">" + test.getQuestionsList().get(answerViewList.get(i).index).questions.get(0) + "</font>" + "&nbsp;"
                            + "<font color=\"#" + color + "\"><a href=\"\" style=\"text-decoration: none; border-bottom: 1px solid #" + color + "; color:#" + color + ";\" >" + textModified[answerViewList.get(i).index] + "</a></font>"
                            + "&nbsp;" + test.getQuestionsList().get(answerViewList.get(i).index).questions.get(1)));

                    break;
                }
                case 3: {
                    RadioGroup rdgr = (RadioGroup) answerViewList.get(i).view;
                    ((RadioButton) rdgr.findViewById(rdgr.getCheckedRadioButtonId())).setChecked(false);
                }
            }
        }

    }

    void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void onClickViews() {


        //listener on baseView to block the listeners from previous fragments
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //done
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateAnswer()) {
                    return;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("You have " + points + "/" + test.getQuestionsList().size() + " correct answers. Do you want to repeat the test?")
                        .setPositiveButton("Repeat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanViews();

//                                if (listenerRefreshUser != null) {
//                                    listenerRefreshUser.getUser(new User("-1", "-1", "-1"));
//                                    if (getActivity() != null) {
//                                        getActivity().getFragmentManager().popBackStack();
//                                    }
//                                } else {
//                                    getFragmentManager().beginTransaction().detach(TestCustomFragment.this).attach(TestCustomFragment.this).commit();
//                                }


                            }
                        })
                        .setNegativeButton("Back to Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeKeyboard();
                                if (points == test.getQuestionsList().size()) {
                                    if (!test.getChecked()) {

                                        //update user
                                        user.setTestProgress(user.getTestProgress() + 1);
                                        user.setTotalPoints(user.getTotalPoints() + 1);

                                        //add test token in user's list of done tests
                                        HashMap<String, String> testsFinished = user.getTestsFinished();
                                        if (testsFinished == null) {
                                            testsFinished = new HashMap<>();
                                        }
                                        testsFinished.put(test.getToken(), "checked");
                                        user.setTestsFinished(testsFinished);

                                        //update user to Firestore
                                        FirestoreDatabase.updateUser(user);

                                        //send back the updated user
                                        if (listenerRefreshUser != null) {
                                            listenerRefreshUser.getUser(user);
                                        }
                                    }
                                }

                                getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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


    class AnswerView {
        int index;
        View view;
        int type;
        int status;
    }
}