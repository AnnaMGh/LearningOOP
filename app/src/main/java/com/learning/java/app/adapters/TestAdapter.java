package com.learning.java.app.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.List;

public class TestAdapter extends ArrayAdapter<Test> {

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public TestAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Test> objects) {
        super(context, resource, textViewResourceId, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_cell, null);
        }

        Test item = getItem(position);
        if (item == null) {
            return convertView;
        }

        RelativeLayout rlCell = convertView.findViewById(R.id.list_cell_layout);
        TextView text = convertView.findViewById(R.id.list_text);
        ImageView checked = convertView.findViewById(R.id.list_img);

        text.setText(item.getTitle());

        //check if test is finished by current user
        if (user != null) {
            if (user.getTestsFinished() != null && user.getTestsFinished().containsKey(item.getToken())) {
                item.setChecked(true);
                checked.setVisibility(View.VISIBLE);
            } else {
                item.setChecked(false);
                checked.setVisibility(View.GONE);
            }
        }

        //check if test is visible for current user
        if (item.isLive() || (user != null && user.getFunction().equals(Constants.ADMIN_FUNCTION))) {
            rlCell.setVisibility(View.VISIBLE);
        } else {
            rlCell.setVisibility(View.GONE);
        }


        return convertView;
    }
}
