package com.learning.java.app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;
import com.learning.java.app.utilities.GlobalSingleton;

public class BaseFragment extends Fragment {

    protected View baseView;
    final protected GlobalSingleton gs = GlobalSingleton.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        gs.mContext = getActivity();
    }

    void replaceFragment(Fragment fragment, String backStackFragmentName) {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

    void addFragment(Fragment fragment, String backStackFragmentName) {
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.add(R.id.container, fragment);
        transaction.addToBackStack(backStackFragmentName);
        transaction.commit();
    }

    void showSimpleAlert(String content) {
        Dialog mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.layout_alert_simple);
        mDialog.setCancelable(true);
        if (mDialog.getWindow() != null) {
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView txt = mDialog.findViewById(R.id.txt);
        txt.setText(content);

        mDialog.show();
    }

    void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    void hideLoadingBar() {
        Activity activity = getActivity();
        if (activity instanceof ContainerActivity) {
            ((ContainerActivity) activity).layoutLoading.setVisibility(View.GONE);
        }
    }

    void showLoadingBar() {
        Activity activity = getActivity();
        if (activity instanceof ContainerActivity) {
            ((ContainerActivity) activity).layoutLoading.setVisibility(View.VISIBLE);
        }
    }
}
