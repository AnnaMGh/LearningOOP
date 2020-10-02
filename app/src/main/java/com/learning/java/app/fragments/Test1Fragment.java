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
import android.widget.Button;
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


public class Test1Fragment extends BaseFragment {

    //views
    TextView titleTextView, doneTextView, exercise2TextView, exercise5TextView1, exercise5TextView2, exercise5TextView3, exercise5TextView4;
    EditText exercise2EditText, exercise3EditText, exercise5EditText1, exercise5EditText2;
    RadioGroup exercise1RadioGroup, exercise4RadioGroup;
    RadioButton exercise11, exercise12, exercise13;
    RadioButton exercise41, exercise42, exercise43;

    //variables
    int points = 0;

    //fragment before
    IUserListener listenerRefreshUser;
    Test test;
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.test1_fragment, container, false);
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
        titleTextView = baseView.findViewById(R.id.test1_title);
        doneTextView = baseView.findViewById(R.id.test1_done_textview);
        exercise2TextView = baseView.findViewById(R.id.test1_exercise2_textview);
        exercise5TextView1 = baseView.findViewById(R.id.test1_exercise5_textview1);
        exercise5TextView2 = baseView.findViewById(R.id.test1_exercise5_textview2);
        exercise5TextView3 = baseView.findViewById(R.id.test1_exercise5_textview3);
        exercise5TextView4 = baseView.findViewById(R.id.test1_exercise5_textview4);
        exercise2EditText = baseView.findViewById(R.id.test1_exercise2_edittext);
        exercise3EditText = baseView.findViewById(R.id.test1_exercise3_edittext);
        exercise5EditText1 = baseView.findViewById(R.id.test1_exercise5_edittext1);
        exercise5EditText2 = baseView.findViewById(R.id.test1_exercise5_edittext2);
        exercise1RadioGroup = baseView.findViewById(R.id.test1_exercise1_radiogroup);
        exercise4RadioGroup = baseView.findViewById(R.id.test1_exercise4_radiogroup);

        exercise11 = baseView.findViewById(R.id.test1_exercise1_radiobtn1);
        exercise12 = baseView.findViewById(R.id.test1_exercise1_radiobtn2);
        exercise13 = baseView.findViewById(R.id.test1_exercise1_radiobtn3);
        exercise41 = baseView.findViewById(R.id.test1_exercise4_radiobtn1);
        exercise42 = baseView.findViewById(R.id.test1_exercise4_radiobtn2);
        exercise43 = baseView.findViewById(R.id.test1_exercise4_radiobtn3);

        exercise2TextView.setText(Html.fromHtml(getResources().getString(R.string.test1021)));
        exercise5TextView1.setText(Html.fromHtml(getResources().getString(R.string.test1051)));
        exercise5TextView2.setText(Html.fromHtml(getResources().getString(R.string.test1052)));
        exercise5TextView3.setText(Html.fromHtml(getResources().getString(R.string.test1053)));
        exercise5TextView4.setText(Html.fromHtml(getResources().getString(R.string.test1054)));

        titleTextView.setText(test.getTitle().toUpperCase());
        int color = getResources().getColor(R.color.colorDarkGray);
        ColorStateList colorStateList = ColorStateList.valueOf(color);

        exercise2EditText.setTextColor(color);
        ViewCompat.setBackgroundTintList(exercise2EditText, colorStateList);

        exercise3EditText.setTextColor(color);
        ViewCompat.setBackgroundTintList(exercise3EditText, colorStateList);

    }

    boolean validateAnswer() {

        points = 0;

        if (exercise1RadioGroup.getCheckedRadioButtonId() == -1 || exercise2EditText.getText().toString().isEmpty()
                || exercise3EditText.getText().toString().isEmpty() || exercise5EditText1.getText().toString().isEmpty()
                || exercise5EditText2.getText().toString().isEmpty() || exercise4RadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), "You did not answered all the questions!", Toast.LENGTH_LONG).show();
            return false;
        }

        //exercise 1
        if (exercise1RadioGroup.getCheckedRadioButtonId() != -1) {
            RadioButton buttonCheck1 = exercise1RadioGroup.findViewById(exercise1RadioGroup.getCheckedRadioButtonId());
            if (buttonCheck1.getText().equals(getResources().getString(R.string.test1010))) {
                buttonCheck1.setTextColor(getResources().getColor(R.color.colorGreen));
                points++;
            } else {
                buttonCheck1.setTextColor(getResources().getColor(R.color.colorRed));
            }
        }

        //exercise 2
        if (exercise2EditText.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test1020))) {
            exercise2EditText.setText(getResources().getString(R.string.test1020)); // we do this because we want to have tre correct case
            exercise2EditText.setTextColor(getResources().getColor(R.color.colorGreen));
            points++;
        } else {

            exercise2EditText.setTextColor(getResources().getColor(R.color.colorRed));
        }

        //exercise 3
        if (exercise3EditText.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test1030))) {
            exercise3EditText.setText(getResources().getString(R.string.test1030)); // we do this because we want to have tre correct case
            exercise3EditText.setTextColor(getResources().getColor(R.color.colorGreen));
            points++;
        } else {
            exercise3EditText.setTextColor(getResources().getColor(R.color.colorRed));
        }

        //exercise 4
        if (exercise4RadioGroup.getCheckedRadioButtonId() != -1) {
            Button buttonCheck4 = baseView.findViewById(exercise4RadioGroup.getCheckedRadioButtonId());
            if (buttonCheck4.getText().equals(getResources().getString(R.string.test1040))) {
                buttonCheck4.setTextColor(getResources().getColor(R.color.colorGreen));
                points++;
            } else {
                buttonCheck4.setTextColor(getResources().getColor(R.color.colorRed));
            }
        }

        //exercise 5
        if (exercise5EditText1.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test10501))
                && exercise5EditText2.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test10502))) {
            exercise5EditText1.setText(getResources().getString(R.string.test10501)); // we do this because we want to have tre correct case
            exercise5EditText2.setText(getResources().getString(R.string.test10502)); // we do this because we want to have tre correct case
            exercise5EditText1.setTextColor(getResources().getColor(R.color.colorGreen));
            exercise5EditText2.setTextColor(getResources().getColor(R.color.colorGreen));
            points++;
        } else if (exercise5EditText1.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test10501))
                || exercise5EditText2.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test10502))) {
            if (exercise5EditText1.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test10501))) {
                exercise5EditText1.setText(getResources().getString(R.string.test10501)); // we do this because we want to have tre correct case
                exercise5EditText1.setTextColor(getResources().getColor(R.color.colorGreen));
            } else {
                exercise5EditText1.setTextColor(getResources().getColor(R.color.colorRed));
            }

            if (exercise5EditText2.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test10502))) {
                exercise5EditText2.setText(getResources().getString(R.string.test10502)); // we do this because we want to have tre correct case
                exercise5EditText2.setTextColor(getResources().getColor(R.color.colorGreen));
            } else {
                exercise5EditText2.setTextColor(getResources().getColor(R.color.colorRed));
            }
        } else {
            exercise5EditText1.setTextColor(getResources().getColor(R.color.colorRed));
            exercise5EditText2.setTextColor(getResources().getColor(R.color.colorRed));
        }

        Toast.makeText(getActivity(), "You have correct answered for " + points + " questions.", Toast.LENGTH_SHORT).show();

        return true;
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

    void cleanViews() {

        exercise11.setChecked(false);
        exercise12.setChecked(false);
        exercise13.setChecked(false);
        exercise41.setChecked(false);
        exercise42.setChecked(false);
        exercise43.setChecked(false);

        exercise2EditText.setText("");
        exercise3EditText.setText("");
        exercise5EditText1.setText("");
        exercise5EditText2.setText("");
    }

    void onClickViews() {
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateAnswer()) {
                    return;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("You have " + points + "/5 correct answers. Do you want to repeat the test?")
                        .setPositiveButton("Repeat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanViews();
                                getFragmentManager().beginTransaction().detach(Test1Fragment.this).attach(Test1Fragment.this).commit();
                            }
                        })
                        .setNegativeButton("Back to Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeKeyboard();
                                if (points == 5) {
                                    if (user.getTestProgress() == 0) {

                                        //update user
                                        user.setTestProgress(1);
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


        exercise2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int color = getResources().getColor(R.color.colorAccent);
                if (!b) {
                    color = getResources().getColor(R.color.colorDarkGray);
                }

                exercise2EditText.setTextColor(color);
                ColorStateList colorStateList = ColorStateList.valueOf(color);
                ViewCompat.setBackgroundTintList(exercise2EditText, colorStateList);
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
