package com.learning.java.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.HashMap;

public class Test2Fragment extends BaseFragment {

    //views
    TextView titleTextView, doneTextView, exercise1TextView1, exercise1TextView2, exercise1TextView3, exercise3TextView,
            exercise4TextView1, exercise4TextView2, exercise4TextView3;
    EditText exercise1EditText, exercise3EditText, exercise4EditText;
    RadioGroup exercise2RadioGroup, exercise5RadioGroup;
    RadioButton exercise21, exercise22, exercise23;
    RadioButton exercise51, exercise52, exercise53;

    //variables
    int points = 0;

    //variables from fragment before
    IUserListener listenerRefreshUser;
    Test test;
    User user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.test2_fragment, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    void initialize() {
        titleTextView = baseView.findViewById(R.id.test2_title);
        doneTextView = baseView.findViewById(R.id.test2_done_textview);
        exercise1TextView1 = baseView.findViewById(R.id.test2_exercise1_textview1);
        exercise1TextView2 = baseView.findViewById(R.id.test2_exercise1_textview2);
        exercise1TextView3 = baseView.findViewById(R.id.test2_exercise1_textview3);
        exercise3TextView = baseView.findViewById(R.id.test2_exercise3_textview);
        exercise4TextView1 = baseView.findViewById(R.id.test2_exercise4_textview1);
        exercise4TextView2 = baseView.findViewById(R.id.test2_exercise4_textview2);
        exercise4TextView3 = baseView.findViewById(R.id.test2_exercise4_textview3);
        exercise1EditText = baseView.findViewById(R.id.test2_exercise1_edittext);
        exercise3EditText = baseView.findViewById(R.id.test2_exercise3_edittext);
        exercise4EditText = baseView.findViewById(R.id.test2_exercise4_edittext);
        exercise2RadioGroup = baseView.findViewById(R.id.test2_exercise2_radiogroup);
        exercise5RadioGroup = baseView.findViewById(R.id.test2_exercise5_radiogroup);

        exercise21 = baseView.findViewById(R.id.test2_exercise2_radiobtn1);
        exercise22 = baseView.findViewById(R.id.test2_exercise2_radiobtn2);
        exercise23 = baseView.findViewById(R.id.test2_exercise2_radiobtn3);
        exercise51 = baseView.findViewById(R.id.test2_exercise5_radiobtn1);
        exercise52 = baseView.findViewById(R.id.test2_exercise5_radiobtn2);
        exercise53 = baseView.findViewById(R.id.test2_exercise5_radiobtn3);

        exercise1TextView1.setText(Html.fromHtml(getResources().getString(R.string.test2011)));
        exercise1TextView2.setText(Html.fromHtml(getResources().getString(R.string.test2012)));
        exercise1TextView3.setText(Html.fromHtml(getResources().getString(R.string.test2013)));
        exercise3TextView.setText(Html.fromHtml(getResources().getString(R.string.test2031)));
        exercise4TextView1.setText(Html.fromHtml(getResources().getString(R.string.test2041)));
        exercise4TextView2.setText(Html.fromHtml(getResources().getString(R.string.test2042)));
        exercise4TextView3.setText(Html.fromHtml(getResources().getString(R.string.test2043)));

        titleTextView.setText(test.getTitle().toUpperCase());


        int color = getResources().getColor(R.color.colorDarkGray);
        ColorStateList colorStateList = ColorStateList.valueOf(color);

        exercise3EditText.setTextColor(color);
        ViewCompat.setBackgroundTintList(exercise3EditText, colorStateList);
    }

    boolean validateAnswer() {

        points = 0;

        if (exercise1EditText.getText().toString().isEmpty() || exercise2RadioGroup.getCheckedRadioButtonId() == -1
                || exercise3EditText.getText().toString().isEmpty() || exercise4EditText.getText().toString().isEmpty()
                || exercise5RadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), "You did not answered all the questions!", Toast.LENGTH_LONG).show();
            return false;
        }

        //exercise 1
        if (exercise1EditText.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test2010))) {
            exercise1EditText.setText(String.valueOf(getResources().getString(R.string.test2010))); // we do this because we want to have tre correct case
            exercise1EditText.setTextColor(getResources().getColor(R.color.colorGreen));
            points++;
        } else {

            exercise1EditText.setTextColor(getResources().getColor(R.color.colorRed));
        }

        //exercise 2
        if (exercise2RadioGroup.getCheckedRadioButtonId() != -1) {
            RadioButton buttonCheck1 = exercise2RadioGroup.findViewById(exercise2RadioGroup.getCheckedRadioButtonId());
            if (buttonCheck1.getText().equals(getResources().getString(R.string.test2020))) {
                buttonCheck1.setTextColor(getResources().getColor(R.color.colorGreen));
                points++;
            } else {
                buttonCheck1.setTextColor(getResources().getColor(R.color.colorRed));
            }
        }

        //exercise 3
        if (exercise3EditText.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test2030))) {
            exercise3EditText.setText(getResources().getString(R.string.test2030)); // we do this because we want to have tre correct
            exercise3EditText.setTextColor(getResources().getColor(R.color.colorGreen));
            points++;
        } else {
            exercise3EditText.setTextColor(getResources().getColor(R.color.colorRed));
        }

        //exercise 4
        if (exercise4EditText.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test2040))) {
            exercise4EditText.setText(getResources().getString(R.string.test2040)); // we do this because we want to have tre correct
            exercise4EditText.setTextColor(getResources().getColor(R.color.colorGreen));
            points++;
        } else {
            exercise4EditText.setTextColor(getResources().getColor(R.color.colorRed));
        }

        //exercise 5
        if (exercise5RadioGroup.getCheckedRadioButtonId() != -1) {
            RadioButton buttonCheck5 = exercise5RadioGroup.findViewById(exercise5RadioGroup.getCheckedRadioButtonId());
            if (buttonCheck5.getText().equals(getResources().getString(R.string.test2050))) {
                buttonCheck5.setTextColor(getResources().getColor(R.color.colorGreen));
                points++;
            } else {
                buttonCheck5.setTextColor(getResources().getColor(R.color.colorRed));
            }
        }

        Toast.makeText(getActivity(), "You have correct answered for " + points + " questions.", Toast.LENGTH_SHORT).show();

        return true;
    }

    void cleanViews() {

        exercise21.setChecked(false);
        exercise22.setChecked(false);
        exercise23.setChecked(false);
        exercise51.setChecked(false);
        exercise52.setChecked(false);
        exercise53.setChecked(false);

        exercise1EditText.setText("");
        exercise3EditText.setText("");
        exercise4EditText.setText("");
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

    void onClickViews() {
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateAnswer()) {
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("You have " + points + "/5 correct answerds. Do you want to repeat the test?")
                        .setPositiveButton("Repeat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanViews();
                                getFragmentManager().beginTransaction().detach(Test2Fragment.this).attach(Test2Fragment.this).commit();
                            }
                        })
                        .setNegativeButton("Back to Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeKeyboard();
                                if (points == 5) {
                                    if (user.getTestProgress() == 1) {

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




        exercise3EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int color = getResources().getColor(R.color.colorAccent);
                if (!b) {
                    color = getResources().getColor(R.color.colorDarkGray);
                }

                exercise3EditText.setTextColor(color);
                ColorStateList colorStateList = ColorStateList.valueOf(color);
                ViewCompat.setBackgroundTintList(exercise3EditText, colorStateList);
            }
        });
    }
}




