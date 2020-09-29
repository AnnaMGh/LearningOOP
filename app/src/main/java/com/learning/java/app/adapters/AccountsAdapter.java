package com.learning.java.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.java.app.R;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IBitmapListener;
import com.learning.java.app.model.User;
import java.util.HashMap;
import java.util.List;

public class AccountsAdapter extends ArrayAdapter<User> {

    public IListAccounts listener;

    public HashMap<String, Bitmap> userPhotos = new HashMap<>();

    public AccountsAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<User> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_cell_accounts, null);
        }

        //set views
        final ImageView img = convertView.findViewById(R.id.listcell_img);
        TextView id = convertView.findViewById(R.id.listcell_id);
        TextView name = convertView.findViewById(R.id.listcell_name);
        TextView learn = convertView.findViewById(R.id.listcell_learn);
        TextView test = convertView.findViewById(R.id.listcell_test);
        TextView points = convertView.findViewById(R.id.listcell_points);
        Button btn = convertView.findViewById(R.id.listcell_btn);

        //get user
        final User user = getItem(position);
        if (user == null) {
            return convertView;
        }

        //set data
        id.setText(String.valueOf("Id: " + user.getId()));
        name.setText(String.valueOf("Name: " + user.getName()));
        learn.setText(String.valueOf("Learn progress: " + user.getLearnProgress()));
        test.setText(String.valueOf("Test progress: " + user.getTestProgress()));
        points.setText(String.valueOf("Total points: " + user.getTotalPoints()));

        //get bitmap
        if (userPhotos.containsKey(user.getToken()) && userPhotos.get(user.getToken()) != null) {
            img.setImageBitmap(userPhotos.get(user.getToken()));
        } else {
            FirestoreDatabase.getPhoto(user, new IBitmapListener() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    userPhotos.put(user.getToken(), bitmap);
                    img.setImageBitmap(bitmap);
                }
            });
        }

        //handle delete click
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRemoveTapCallback(position);
                }
            }
        });

        return convertView;
    }

    public interface IListAccounts {
        void onRemoveTapCallback(int position);
    }
}
