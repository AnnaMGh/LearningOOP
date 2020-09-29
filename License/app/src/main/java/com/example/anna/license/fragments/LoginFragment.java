package com.learning.java.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.User;

public class LoginFragment extends BaseFragment {

    EditText usernameEt, passwordEt;
    Button loginBtn;
    TextView registerTxtV;
    RelativeLayout facebookLayout;

    SqliteHelper database;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_login, container, false);

        initialize();
        setOnClickViews();

        return baseView;
    }

    void initialize() {
        usernameEt = baseView.findViewById(R.id.login_username);
        passwordEt = baseView.findViewById(R.id.login_password);
        loginBtn = baseView.findViewById(R.id.login_login_btn);
        registerTxtV = baseView.findViewById(R.id.login_register);
        facebookLayout = baseView.findViewById(R.id.login_facebook);

        database = SqliteHelper.getInstance(getActivity());
        settings = getActivity().getSharedPreferences("Learning_java", 0);
        editor = settings.edit();
    }

    void setOnClickViews() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEt.getText().toString().isEmpty() || passwordEt.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "You didn't complete all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                database = SqliteHelper.getInstance(getActivity());
                user = database.getUserByName(usernameEt.getText().toString());
                if (user == null) {

                    Toast.makeText(getActivity(), "User incorrect!", Toast.LENGTH_LONG).show();
                    return;
                }else if (!user.getPassword().equals(passwordEt.getText())) {
                    Toast.makeText(getActivity(), "Password incorrect!", Toast.LENGTH_LONG).show();
                    return;
                }

                settings = getActivity().getSharedPreferences("Learning_java", 0);
                editor = settings.edit();
                editor.putString("ACCOUNT_KEY", String.valueOf(database.getUserIdByName(usernameEt.getText().toString(), passwordEt.getText().toString())));
                editor.putString("ACCOUNT_NAME", usernameEt.getText().toString());
                editor.putString("ACCOUNT_TYPE", "database");
                editor.putString("ACCOUNT_FUNCTION", database.checkUserFunction(usernameEt.getText().toString(), passwordEt.getText().toString()));
                editor.apply();

                addFragment(new MainMenuFragment(), "MainMenuFragment");

            }
        });

        registerTxtV.setText(Html.fromHtml(getResources().getString(R.string.sing_up)));
        registerTxtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new RegisterFragment(), "RegisterFragment");
            }
        });
        facebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContainerActivity activity = (ContainerActivity) getActivity();
                activity.processFacebookLogin(new IFacebookListener() {
                    @Override
                    public void getFacebookData(String id, String name, String email) {
                        user = database.getUserByName(name);
                        if (user == null) {
                            user = new User(name, email, "", "user", 1, 0, 0, 1, 1);
                            database.addUser(user);
                        }

                        SharedPreferences settings = getActivity().getSharedPreferences("Learning_java", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ACCOUNT_KEY", String.valueOf(database.getUserIdByUser(user)));
                        editor.putString("ACCOUNT_NAME", name);
                        editor.putString("ACCOUNT_TYPE", "facebook");
                        editor.putString("ACCOUNT_FUNCTION", "user");
                        editor.apply();

                        addFragment(new MainMenuFragment(), "MainMenuFragment");
                    }
                });
            }
        });
    }

    void addFragment(Fragment fragment, String backStackFragmentName) {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

    public interface IFacebookListener {
        void getFacebookData(String id, String name, String email);
    }
}
