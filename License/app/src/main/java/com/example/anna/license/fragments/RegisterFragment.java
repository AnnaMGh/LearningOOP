package com.learning.java.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.User;

import java.util.List;

public class RegisterFragment extends BaseFragment {

    EditText usernameEt, emailEt, passwordEt, repeatPasswordEt;
    ImageView checkImgV1, checkImgV2;
    TextView checkTxtV1, checkTxtV2;
    Button registerBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_register, container, false);

        initialize();
        setOnClickViews();

        return baseView;
    }

    void initialize() {

        usernameEt = (EditText) baseView.findViewById(R.id.register_username);
        emailEt = (EditText) baseView.findViewById(R.id.register_email);
        passwordEt = (EditText) baseView.findViewById(R.id.register_password);
        repeatPasswordEt = (EditText) baseView.findViewById(R.id.register_password2);
        checkImgV1 = (ImageView) baseView.findViewById(R.id.register_check_imgv);
        checkImgV2 = (ImageView) baseView.findViewById(R.id.register_check_imgv2);
        checkTxtV1 = (TextView) baseView.findViewById(R.id.register_check_txtv);
        checkTxtV2 = (TextView) baseView.findViewById(R.id.register_check_txtv2);
        registerBtn = (Button) baseView.findViewById(R.id.register_signup_btn);
    }

    void setOnClickViews() {

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password1Validation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        repeatPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password2Validation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usernameEt.getText().toString().isEmpty() || emailEt.getText().toString().isEmpty()
                        || passwordEt.getText().toString().isEmpty() || repeatPasswordEt.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "You did not complete all the fields!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!password1Validation() || !password2Validation()) {
                    Toast.makeText(getActivity(), "Password don't matched!!", Toast.LENGTH_LONG).show();
                    return;
                }

                User user;
                SqliteHelper database = SqliteHelper.getInstance(getActivity());
                if (usernameEt.getText().toString().equals("admin")) {
                    user = new User(usernameEt.getText().toString(), emailEt.getText().toString(), passwordEt.getText().toString(),
                            "admin", 1, 1, 0, 1, 1);
                } else {
                    user = new User(usernameEt.getText().toString(), emailEt.getText().toString(), passwordEt.getText().toString(),
                            "user", 1, 1, 0, 1, 1);
                }

                if (database.checkUserByName(user)) {
                    Toast.makeText(getActivity(), "User name exist!", Toast.LENGTH_LONG).show();
                    return;
                }

                database.addUser(user);

                if (password1Validation() && password2Validation()) {
                    SharedPreferences settings = getActivity().getSharedPreferences("Learning_java", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("ACCOUNT_KEY", String.valueOf(database.getUserIdByUser(user)));
                    editor.putString("ACCOUNT_NAME", usernameEt.getText().toString());
                    editor.putString("ACCOUNT_TYPE", "database");
                    editor.putString("ACCOUNT_FUNCTION", database.checkUserFunction(user.getName(), user.getPassword()));
                    editor.commit();

                    addFragment(new MainMenuFragment(), "MainMenuFragment");
                }
            }
        });

    }


    boolean password1Validation() {

        if (hasNumbers(passwordEt.getText().toString().toCharArray()) && hasCapLetter(passwordEt.getText().toString().toCharArray())) {
            if (passwordEt.getText().length() > 6) {
                checkImgV1.setVisibility(View.VISIBLE);
                checkTxtV1.setVisibility(View.VISIBLE);
                checkImgV1.setImageResource(R.drawable.check_green);
                checkTxtV1.setTextColor(getResources().getColor(R.color.colorGreenAnswer));
                checkTxtV1.setText("Strong password");
            } else if (passwordEt.getText().length() > 3 && passwordEt.getText().length() < 7) {
                checkImgV1.setVisibility(View.VISIBLE);
                checkTxtV1.setVisibility(View.VISIBLE);
                checkImgV1.setImageResource(R.drawable.check_yellow);
                checkTxtV1.setTextColor(getResources().getColor(R.color.colorYellowAnswer));
                checkTxtV1.setText("OK password");
            } else {
                checkImgV1.setVisibility(View.VISIBLE);
                checkTxtV1.setVisibility(View.VISIBLE);
                checkImgV1.setImageResource(R.drawable.check_red);
                checkTxtV1.setTextColor(getResources().getColor(R.color.colorRedAnswer));
                checkTxtV1.setText("Week password: Needs more than 4 characters.");
                return false;
            }
        } else {
            checkImgV1.setVisibility(View.VISIBLE);
            checkTxtV1.setVisibility(View.VISIBLE);
            checkImgV1.setImageResource(R.drawable.check_red);
            checkTxtV1.setTextColor(getResources().getColor(R.color.colorRedAnswer));
            checkTxtV1.setText("Week password: Needs numbers and capitalized letters.");
            return false;
        }
        return true;
    }

    boolean password2Validation() {
        if (repeatPasswordEt.getText().toString().equals(passwordEt.getText().toString())) {
            checkImgV2.setVisibility(View.VISIBLE);
            checkTxtV2.setVisibility(View.GONE);
            checkImgV2.setImageResource(R.drawable.check_green);
            checkTxtV2.setTextColor(getResources().getColor(R.color.colorGreenAnswer));
            checkTxtV2.setText("Passwords match.");
            return true;
        } else {
            checkImgV2.setVisibility(View.VISIBLE);
            checkTxtV2.setVisibility(View.VISIBLE);
            checkImgV2.setImageResource(R.drawable.check_red);
            checkTxtV2.setTextColor(getResources().getColor(R.color.colorRedAnswer));
            checkTxtV2.setText("Passwords don't match.");
            return false;
        }
    }

    boolean hasNumbers(char[] s) {
        for (char c : s) {
            for (int i = 0; i < 10; i++) {
                if ((c + "").equals(i + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean hasCapLetter(char[] word) {
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z'};

        for (char character : word) {
            for (int i = 0; i < letters.length; i++) {
                if ((character + "").equals(letters[i] + "")) {
                    return true;
                }
            }
        }

        return false;
    }

    void addFragment(Fragment fragment, String backStackFragmentName) {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }
}
