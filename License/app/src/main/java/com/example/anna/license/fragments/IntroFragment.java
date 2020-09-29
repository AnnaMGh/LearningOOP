package com.learning.java.app.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.utilities.OnSwipeTouchListener;
import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;

import java.util.Timer;
import java.util.TimerTask;

public class IntroFragment extends BaseFragment {

    TextView learningTxtV, swipeTxtV;
    LinearLayout movingLayout;
    RelativeLayout mainLayout;
    AnimatorSet animatorSet;
    boolean animatorStatus;
    int widthWindow, heightWindow, widthView, heightView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_intro, container, false);

        initializeViews();
        getWindowDimension();
        getViewDimensions();
        timer();
        swipe();

        return baseView;
    }


    void initializeViews() {
        learningTxtV = baseView.findViewById(R.id.intro_learning_txtv);
        swipeTxtV = baseView.findViewById(R.id.intro_swipe_txtv);
        mainLayout = baseView.findViewById(R.id.intro_main_layout);
        movingLayout = baseView.findViewById(R.id.intro_moving_layout);
    }

    void getWindowDimension() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthWindow = size.x;
        heightWindow = size.y;
    }

    void getViewDimensions() {
        ViewTreeObserver viewTreeObserver = learningTxtV.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        learningTxtV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    widthView = learningTxtV.getWidth();
                    heightView = learningTxtV.getHeight();

                    animationStart();
                }
            });
        }
    }

    void animationStart() {
        movingLayout.setY(-300);
        learningTxtV.setX(-widthView);

        ObjectAnimator animatorImg = ObjectAnimator.ofFloat(movingLayout, "y", heightWindow / 4);
        animatorImg.setDuration(1500);

        ObjectAnimator animatorTxtV = ObjectAnimator.ofFloat(learningTxtV, "x", widthWindow / 2 - widthView / 2);
        animatorTxtV.setDuration(1500);

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorImg, animatorTxtV);
        animatorSet.start();

        animatorStatus = true;
    }

    void timer() {
        final Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e("anim", "still runing");
                if (animatorStatus) {
                    if (!animatorSet.isRunning()) {
                        myTimer.cancel();

                        startActivity(new Intent(getActivity(), ContainerActivity.class));
                    }
                }
            }
        }, 1 * 05 * 500, 1 * 10 * 200);

    }

    @SuppressLint("ClickableViewAccessibility")
    void swipe() {
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
//                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
//                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
//                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), ContainerActivity.class));


            }

            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
            }

        });
    }
}