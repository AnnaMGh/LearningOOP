package com.learning.java.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.User;

import java.util.ArrayList;


public class LessonsFragment extends BaseFragment {

    //views
    Button[] buttons;
    Button nextButton, previousButton;
    TextView text, codeText;
    ImageView image;
    RadioGroup mRadioGroup;
    RadioButton[] mRadioButtons;
    RadioButton mRadioBtnAnsw;

    //variables from previous fragment
    User user;
    int level1, level2;
    String learn;

    //variables
    int numberOfVisibleButtons, numberOfFinishedButtons;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.lessons_fragment, container, false);

        if (user == null) {
            FirestoreDatabase.getUserBy(Constants.TOKEN_KEY, gs.getString(Constants.TOKEN_KEY), new IUserListener() {
                @Override
                public void getUser(User receivedUser) {
                    user = receivedUser;
                    initialize();
                    getLevel();
                    onClickViews();
                }

                @Override
                public void getAllUsers(ArrayList<User> userList) {

                }

                @Override
                public void getUserToken(String token) {

                }
            });
        } else {
            initialize();
            getLevel();
            onClickViews();
        }

        return baseView;
    }

    void initialize() {
        buttons = new Button[4];
        mRadioButtons = new RadioButton[3];
        buttons[0] = baseView.findViewById(R.id.learn2_button1);
        buttons[1] = baseView.findViewById(R.id.learn2_button2);
        buttons[2] = baseView.findViewById(R.id.learn2_button3);
        buttons[3] = baseView.findViewById(R.id.learn2_button4);
        nextButton = baseView.findViewById(R.id.learn2_button_next);
        previousButton = baseView.findViewById(R.id.learn2_button_previous);
        text = baseView.findViewById(R.id.learn2_text);
        codeText = baseView.findViewById(R.id.learn2_code_text);
        image = baseView.findViewById(R.id.learn2_image);
        mRadioGroup = baseView.findViewById(R.id.learn2_radio_group);
        mRadioButtons[0] = baseView.findViewById(R.id.learn2_radio_button1);
        mRadioButtons[1] = baseView.findViewById(R.id.learn2_radio_button2);
        mRadioButtons[2] = baseView.findViewById(R.id.learn2_radio_button3);

        mRadioGroup.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
    }

    void getLevel() {
        numberOfVisibleButtons = 1;
        numberOfFinishedButtons = 1;

        for (int i = 0; i <= 2; i++) {
            if (getResources().getIdentifier("learn" + learn + (i + 1), "string", getActivity().getPackageName()) != 0) {
                buttons[i].setVisibility(View.VISIBLE);
                numberOfVisibleButtons++;
            } else {
                buttons[i].setVisibility(View.GONE);
            }
        }
    }

    void formatLayout(int nr, String s) {
        Log.e("learn2", "learn" + learn + nr);

        int resource = getResources().getIdentifier("learn" + learn + nr, "string", getActivity().getPackageName());

        //check if resource exists
        if (resource != 0) {
            getActivity().getFragmentManager().popBackStack("MenuLearnFragment", 0);
        }

        text.setText(Html.fromHtml(getResources().getString(resource)));

        if (getResources().getIdentifier("learn" + learn + nr, "drawable", getActivity().getPackageName()) != 0) {
            image.setImageResource(getResources().getIdentifier("learn" + learn + nr, "drawable", getActivity().getPackageName()));
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }
        if (getResources().getIdentifier("codelearn" + learn + nr, "string", getActivity().getPackageName()) != 0) {
            codeText.setVisibility(View.VISIBLE);
            codeText.setText(Html.fromHtml(getResources().getString(getResources().getIdentifier("codelearn" + learn + nr, "string", getActivity().getPackageName()))));
        } else {
            codeText.setText("");
            codeText.setVisibility(View.GONE);
        }

        switch (s) {
            case "next":
                buttons[nr - 1].setBackgroundResource(R.drawable.button_template);
                break;
            case "last":
                buttons[3].setBackgroundResource(R.drawable.button_template_gray);
                break;
            default:
                buttons[nr].setBackgroundResource(R.drawable.button_template_gray);
                break;
        }
    }

    void onClickViews() {

        //base view click blocked
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //back
        baseView.findViewById(R.id.txt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack("LearnFragment", 0);
            }
        });


        formatLayout(1, "next");

        //next
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfFinishedButtons < (numberOfVisibleButtons - 1)) {
                    numberOfFinishedButtons++;
                    formatLayout(numberOfFinishedButtons, "next");
                    //Toast.makeText(Learn2Activity.this, (numberOfFinishedButtons-1)+ "<" + numberOfVisibleButtons + "-1", Toast.LENGTH_LONG).show();
                } else if (numberOfFinishedButtons == (numberOfVisibleButtons - 1)) {

                    numberOfFinishedButtons++;
                    formatLayout(4, "next");
                    mRadioGroup.setVisibility(View.VISIBLE);
                    for (int i = 0; i < 3; i++) {
                        mRadioButtons[i].setText(getResources().getString(getResources().getIdentifier("learn" + learn + 4 + (i + 1), "string", getActivity().getPackageName())));
                    }
                    //Toast.makeText(Learn2Activity.this, (numberOfFinishedButtons-1) + "==" + numberOfVisibleButtons + "-1", Toast.LENGTH_LONG).show();
                } else if (numberOfFinishedButtons == numberOfVisibleButtons) {
                    mRadioBtnAnsw = baseView.findViewById(mRadioGroup.getCheckedRadioButtonId());

                    try {
                        //if answer is correct
                        if (mRadioBtnAnsw.getText().equals(getResources().getString(getResources().getIdentifier("learn" + learn + 4 + 0, "string", getActivity().getPackageName())))) {
                            Toast.makeText(getActivity(), "Correct answer!", Toast.LENGTH_SHORT).show();

                            //if is the first time when this test it is done update user
                            if (level2 == user.getLearnProgress() - 6 * (level1 - 1)) {
                                user.setLearnProgress(user.getLearnProgress() + 1);
                                user.setTotalPoints(user.getTotalPoints() + 1);

                                FirestoreDatabase.updateUserWithCallback(user, new IRefreshListener() {
                                    @Override
                                    public void doRefresh(boolean doRefresh) {
                                        if (level2 == 6) {
                                            getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
                                        } else {
                                            getActivity().getFragmentManager().popBackStack("LearnFragment", 0);
                                        }
                                    }
                                });
                            }
                            //else just go back to menu or previous fragment
                            else {
                                if (level2 == 6) {
                                    getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
                                } else {
                                    getActivity().getFragmentManager().popBackStack("LearnFragment", 0);
                                }


                            }
                        } else {
                            Toast.makeText(getActivity(), "Not the correct answer!", Toast.LENGTH_SHORT).show();
                            numberOfFinishedButtons -= 1;
                            formatLayout(numberOfFinishedButtons, "last");
                            mRadioGroup.clearCheck();
                            mRadioGroup.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), "Please choose an answer!", Toast.LENGTH_SHORT).show();
                    }


                    //si marim progresul ca sa se deblocheze urmatorul nivel
                    //Toast.makeText(Learn2Activity.this, numberOfFinishedButtons+ "==" + numberOfVisibleButtons, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getActivity(), "I don't know yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //previous
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadioGroup.setVisibility(View.GONE);
                if (numberOfFinishedButtons > 1) {
                    numberOfFinishedButtons--;
                    if (numberOfFinishedButtons + 1 == numberOfVisibleButtons) {
                        formatLayout(numberOfFinishedButtons, "last");
                    } else {
                        formatLayout(numberOfFinishedButtons, "previous");
                    }
                }
            }
        });
    }

}
