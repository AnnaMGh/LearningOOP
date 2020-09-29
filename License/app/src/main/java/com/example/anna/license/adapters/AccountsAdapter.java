package com.learning.java.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.learning.java.app.R;
import com.learning.java.app.model.User;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class AccountsAdapter extends ArrayAdapter<User> {

    public IListAccounts listener;

    public AccountsAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<User> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_cell_accounts, null);
        }

        ImageView img = convertView.findViewById(R.id.listcell_img);
        TextView id = convertView.findViewById(R.id.listcell_id);
        TextView name = convertView.findViewById(R.id.listcell_name);
        TextView learn = convertView.findViewById(R.id.listcell_learn);
        TextView test = convertView.findViewById(R.id.listcell_test);
        TextView points = convertView.findViewById(R.id.listcell_points);
        Button btn = convertView.findViewById(R.id.listcell_btn);

        final User user = getItem(position);

        String idTxt = "Id: " + user.getId();
        String nameTxt = "Name: " + user.getName();
        String learnTxt = "Learn progress: " + user.getLearnProgress();
        String testTxt = "Test progress: " + user.getTestProgress();
        String pointsTxt = "Total points: " + user.getTotalPoints();
        String image = user.getPhoto();

        id.setText(idTxt);
        name.setText(nameTxt);
        learn.setText(learnTxt);
        test.setText(testTxt);
        points.setText(pointsTxt);

        img.setImageBitmap(decodeBase64(image));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null) {
                    listener.getUser(user);
                }
            }
        });

        return convertView;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public interface IListAccounts {

        void getUser(User user);
    }
}
