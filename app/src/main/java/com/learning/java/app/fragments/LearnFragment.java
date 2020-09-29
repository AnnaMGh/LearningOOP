package com.learning.java.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.User;

import java.util.ArrayList;


public class LearnFragment extends BaseFragment {

    //views
    Button[] buttons = new Button[6];

    //variables from previous fragment
    User user;
    int level;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_learn, container, false);

        if (user == null) {
            FirestoreDatabase.getUserBy(Constants.TOKEN_KEY, gs.getString(Constants.TOKEN_KEY), new IUserListener() {
                @Override
                public void getUser(User receivedUser) {
                    user = receivedUser;
                    initialize();
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
            onClickViews();
        }

        return baseView;
    }

    void initialize() {
        buttons[0] = baseView.findViewById(R.id.learn_btn1);
        buttons[1] = baseView.findViewById(R.id.learn_btn2);
        buttons[2] = baseView.findViewById(R.id.learn_btn3);
        buttons[3] = baseView.findViewById(R.id.learn_btn4);
        buttons[4] = baseView.findViewById(R.id.learn_btn5);
        buttons[5] = baseView.findViewById(R.id.learn_btn6);

        getLevel();
        setLevel();
    }

    void getLevel() {
        for (int i = 0; i < 6; i++) {
            try {
                buttons[i].setText(getResources().getIdentifier("learn" + (level + 1) + "" + (i + 1), "string", getActivity().getPackageName()));
            } catch (Exception e) {
                Log.e("CRASH", "LearnFragment | getLevel | learn" + (level + 1) + "" + (i + 1) + ": " + e.getMessage());
                e.printStackTrace();
                getActivity().getFragmentManager().popBackStack();
            }
        }
    }

    void setLevel() {

        int[] cases = new int[6];
        for (int i = 0; i < 6; i++) {
            // cases[i]=(level+1)*(i+1);
            cases[i] = (i + 1) + 6 * level;
        }

        for (int i = 1; i < 6; i++) {
            if (user.getLearnProgress() >= cases[i]) {
                buttons[i].setBackground(getResources().getDrawable(R.drawable.rounded_darkblue));
            }
        }
    }


    void setButtonListener(Button btn, final int index) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((index) + 6 * level > user.getLearnProgress()) {

                    Toast.makeText(getActivity(), "Unlocked lesson", Toast.LENGTH_SHORT).show();
                    return;
                }

                LessonsFragment fragmentLessons = new LessonsFragment();
                fragmentLessons.user = user;
                fragmentLessons.level1 = level + 1;
                fragmentLessons.level2 = index;
                fragmentLessons.learn = String.valueOf(level) + String.valueOf(index);
                replaceFragment(fragmentLessons, "LessonsFragment");
            }
        });
    }

    void onClickViews() {
        //base view
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //buttons
        for (int i = 0; i < 6; i++) {
            setButtonListener(buttons[i], i + 1);
        }

        //back
        baseView.findViewById(R.id.txt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
            }
        });
    }

}
