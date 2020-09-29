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
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.User;


public class LearnFragment extends BaseFragment {

    SqliteHelper database;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    User user;

    Button[] buttons = new Button[6];
    Bundle bundle;
    int level;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_learn, container, false);
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
        buttons[0] = baseView.findViewById(R.id.learn_btn1);
        buttons[1] = baseView.findViewById(R.id.learn_btn2);
        buttons[2] = baseView.findViewById(R.id.learn_btn3);
        buttons[3] = baseView.findViewById(R.id.learn_btn4);
        buttons[4] = baseView.findViewById(R.id.learn_btn5);
        buttons[5] = baseView.findViewById(R.id.learn_btn6);

        bundle = getArguments();

        settings = getActivity().getSharedPreferences("Learning_java", 0);
        editor = settings.edit();
        database = SqliteHelper.getInstance(getActivity());
        user = database.getUser(Integer.parseInt(settings.getString("ACCOUNT_KEY", null)));

        getLevel();
        setLevel();
    }

    void getLevel() {

        bundle = getArguments();
        level = bundle.getInt("level");
        Log.e("learn", String.valueOf(level));
        for (int i = 0; i < 6; i++) {
            buttons[i].setText(getResources().getIdentifier("learn" + (level + 1) + "" + (i + 1), "string", getActivity().getPackageName()));
        }
    }

    void setLevel() {

        int[] cases = new int[6];
        for(int i=0; i<6; i++)
        {
           // cases[i]=(level+1)*(i+1);
            cases[i]=(i+1)+6*level;
        }

        for(int i=1; i<6; i++)
        {
            if (user.getLearnProgress()>=cases[i])
            {
                buttons[i].setBackground(getResources().getDrawable(R.drawable.rounded_darkblue));
            }
        }
    }


    void setButtonListener(Button btn, final int index) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((index)+6*level > user.getLearnProgress()) {

                    Toast.makeText(getActivity(), "Unlocked lesson", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle b = new Bundle();
                b.putInt("level1", level + 1);
                b.putInt("level2", index);
                b.putString("learn", String.valueOf(level) + String.valueOf(index));
                addFragment(new LessonsFragment(), "LessonsFragment", b);
            }
        });
    }

    void onClickViews() {
        for (int i = 0; i < 6; i++) {
            setButtonListener(buttons[i], i + 1);
        }
    }

    void addFragment(Fragment fragment, String backStackFragmentName, Bundle bundle) {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.setArguments(bundle);
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

}
