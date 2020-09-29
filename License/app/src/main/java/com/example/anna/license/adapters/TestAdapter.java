package com.learning.java.app.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learning.java.app.R;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.List;

public class TestAdapter extends ArrayAdapter<Test> {


    SqliteHelper database;
    SharedPreferences settings;
    ;
    User user;


    public TestAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Test> objects) {
        super(context, resource, textViewResourceId, objects);
        database = SqliteHelper.getInstance(getContext());
        settings = getContext().getSharedPreferences("Learning_java", 0);
        user = database.getUser(Integer.parseInt(settings.getString("ACCOUNT_KEY", null)));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_cell, null);
        }

        Test item = getItem(position);

        RelativeLayout layout = convertView.findViewById(R.id.list_cell_layout);
        TextView text = convertView.findViewById(R.id.list_text);
        ImageView checked = convertView.findViewById(R.id.list_img);

        text.setText(item.getTitle());


        if (position < user.getTestProgress()) {
            checked.setVisibility(View.VISIBLE);
        } else {
            checked.setVisibility(View.GONE);
        }


        return convertView;
    }
}
