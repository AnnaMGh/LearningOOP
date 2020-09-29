package com.learning.java.app.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.model.User;

public class UserProfileFragment extends BaseFragment {

    //variables from previous fragment
    public User user;
    public Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    private void initialize() {
        //photo
        ((ImageView) baseView.findViewById(R.id.user_profile_img)).setImageBitmap(bitmap);

        //name
        if (user.getToken().equals(gs.getString(Constants.TOKEN_KEY))) {
            ((TextView) baseView.findViewById(R.id.user_profile_name_txt)).setText(String.valueOf(user.getName() + "(Me)"));
        } else {
            ((TextView) baseView.findViewById(R.id.user_profile_name_txt)).setText(user.getName());
        }


        //points
        ((TextView) baseView.findViewById(R.id.user_profile_points_txt)).setText(Html.fromHtml("Points: <font color=\"#000000\">" + user.getTotalPoints() + "</font>"));
    }

    private void onClickViews() {

        //block event from layout
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //back
        baseView.findViewById(R.id.txt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack("MainMenuFragment", 0);
            }
        });
    }
}
