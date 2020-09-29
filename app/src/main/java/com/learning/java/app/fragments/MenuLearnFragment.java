package com.learning.java.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.User;

import java.util.ArrayList;

public class MenuLearnFragment extends BaseFragment {

    //views
    LinearLayout learnFooter;
    Button addLessonBtn, removeLessonBtn;
    ImageView[] learnImgV;
    RelativeLayout parentLyt;

    //variables from previous fragment
    User user;

    //variables
    int nrOfLessons;
    int[] imgResources = {R.drawable.learn_level_base, R.drawable.learn_level_ifelse, R.drawable.learn_level_for_loop, R.drawable.learn_level_oop, R.drawable.learn_level_more_oop};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_menu_learn, container, false);

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

        learnFooter = baseView.findViewById(R.id.menulearn_footer);
        addLessonBtn = baseView.findViewById(R.id.menulearn_add_btn);
        removeLessonBtn = baseView.findViewById(R.id.menulearn_remove_btn);
        parentLyt = baseView.findViewById(R.id.menulearn_parent_lyt);
        nrOfLessons = parentLyt.getChildCount();

        learnImgV = new ImageView[nrOfLessons];
        for (int i = 0; i < nrOfLessons; i++) {
            learnImgV[i] = baseView.findViewById(getResources().getIdentifier("menulearn_img" + (i + 1), "id", getActivity().getPackageName()));
        }

        setLevels();
    }

    void setLevels() {

        int target = 1;
        int[] cases = new int[5];
        for (int i = 0; i < 5; i++) {
            cases[i] = target;
            target += 6;
        }

//        Toast.makeText(getActivity(), user.getLearnProgress() + " |  " + cases[0] + " " + cases[1] + " " + cases[2] + " " + cases[3] + " " + cases[4], Toast.LENGTH_SHORT).show();
        for (int i = 1; i < 5; i++) {
            if (user.getLearnProgress() >= cases[i]) {
                learnImgV[i].setImageResource(imgResources[i]);
            }
        }
    }

    void onClickViews() {
        for (int i = 0; i < nrOfLessons; i++) {
            setOnImgClickListener(learnImgV[i], i);
        }
    }

    void setOnImgClickListener(ImageView img, final int index) {
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index * 6 + 1 > user.getLearnProgress()) {
                    learnImgV[index].setImageResource(R.drawable.level_lock);
                    Toast.makeText(getActivity(), "Unlocked lesson", Toast.LENGTH_SHORT).show();
                    return;
                }

                LearnFragment fragmentLearn = new LearnFragment();
                fragmentLearn.user = user;
                fragmentLearn.level = index;
                replaceFragment(fragmentLearn, "LearnFragment");
            }
        });
    }

}
