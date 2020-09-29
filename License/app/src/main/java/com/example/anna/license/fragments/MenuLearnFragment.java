package com.learning.java.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.User;

public class MenuLearnFragment extends BaseFragment {

    LinearLayout learnFooter;
    Button addLessonBtn, removeLessonBtn;
    ImageView[] learnImgV;
    RelativeLayout parentLyt;
    int nrOfLessons;

    SqliteHelper database;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    User user;


    int[] imgResources = {R.drawable.base, R.drawable.ifelse, R.drawable.for_loop, R.drawable.oop, R.drawable.more_oop};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_menu_learn, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    void initialize() {

        learnFooter = baseView.findViewById(R.id.menulearn_footer);
        addLessonBtn = baseView.findViewById(R.id.menulearn_add_btn);
        removeLessonBtn = baseView.findViewById(R.id.menulearn_remove_btn);
        parentLyt = baseView.findViewById(R.id.menulearn_parent_lyt);
        nrOfLessons = parentLyt.getChildCount();

        Log.e("nr", nrOfLessons + "");

        learnImgV = new ImageView[nrOfLessons];
        for (int i = 0; i < nrOfLessons; i++) {
            learnImgV[i] = baseView.findViewById(getResources().getIdentifier("menulearn_img" + (i + 1), "id", getActivity().getPackageName()));
        }

        database = SqliteHelper.getInstance(getActivity());
        settings = getActivity().getSharedPreferences("Learning_java", 0);
        editor = settings.edit();
        user = database.getUser(Integer.parseInt(settings.getString("ACCOUNT_KEY", null)));

        if(user!=null)
        setLevels();

    }

    void setLevels() {

        int target = 1;
        int[] cases = new int[5];
        for (int i = 0; i < 5; i++) {
            cases[i] = target;
            target += 6;

        }

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

                if (index * 6+1 > user.getLearnProgress()) {
                    Toast.makeText(getActivity(), "Unlocked lesson", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putInt("level", index);
                addFragment(new LearnFragment(), "LearnFragment", bundle);
            }
        });
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
