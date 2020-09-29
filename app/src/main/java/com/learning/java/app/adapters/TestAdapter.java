package com.learning.java.app.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        TextView text = convertView.findViewById(R.id.list_text);
        ImageView checked = convertView.findViewById(R.id.list_img);

        if (item != null) {
            text.setText(item.getTitle());
        }


        if (user != null) {
            if (position < user.getTestProgress()) {
                checked.setVisibility(View.VISIBLE);
            } else {
                checked.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
