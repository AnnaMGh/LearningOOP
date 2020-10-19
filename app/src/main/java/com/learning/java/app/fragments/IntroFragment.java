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

import com.learning.java.app.utilities.OnSwipeTouchListener;
import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;

import java.util.Timer;
import java.util.TimerTask;

public class IntroFragment extends BaseFragment {

    //views
    TextView learningTxtV, swipeTxtV;
    LinearLayout movingLayout;
    RelativeLayout mainLayout;
    LinearLayout llIntroText;

    //variables
    Timer myTimer;
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


    private void initializeViews() {
        learningTxtV = baseView.findViewById(R.id.intro_learning_txtv);
        llIntroText = baseView.findViewById(R.id.intro_text);
        swipeTxtV = baseView.findViewById(R.id.intro_swipe_txtv);
        mainLayout = baseView.findViewById(R.id.intro_main_layout);
        movingLayout = baseView.findViewById(R.id.intro_moving_layout);
    }

    private void getWindowDimension() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthWindow = size.x;
        heightWindow = size.y;
    }

    private void getViewDimensions() {
        ViewTreeObserver viewTreeObserver = llIntroText.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        llIntroText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    widthView = llIntroText.getWidth();
                    heightView = llIntroText.getHeight();

                    animationStart();
                }
            });
        }
    }

    private void animationStart() {
        movingLayout.setY(-300);
        llIntroText.setX(-widthView);

        ObjectAnimator animatorImg = ObjectAnimator.ofFloat(movingLayout, "y", heightWindow / 2.15f);
        animatorImg.setDuration(1500);

        ObjectAnimator animatorTxtV = ObjectAnimator.ofFloat(llIntroText, "x", widthWindow / 2f - widthView / 2f);
        animatorTxtV.setDuration(1500);

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorImg, animatorTxtV);
        animatorSet.start();

        animatorStatus = true;
    }

    private void timer() {
        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e("anim", "still runing");
                if (animatorStatus) {
                    if (!animatorSet.isRunning()) {
                        myTimer.cancel();
                        if (getActivity() != null) {
                            startActivity(new Intent(getActivity(), ContainerActivity.class));
                        } else {
                            System.exit(0);
                        }
                    }
                }
            }
        }, 1 * 05 * 500, 1 * 10 * 200);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipe() {
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                animatorSet.cancel();
                myTimer.cancel();
                startActivity(new Intent(getActivity(), ContainerActivity.class));
            }

            public void onSwipeRight() {
                animatorSet.cancel();
                myTimer.cancel();
                startActivity(new Intent(getActivity(), ContainerActivity.class));
            }

            public void onSwipeLeft() {
                animatorSet.cancel();
                myTimer.cancel();
                startActivity(new Intent(getActivity(), ContainerActivity.class));
            }

            public void onSwipeBottom() {
                animatorSet.cancel();
                myTimer.cancel();
                startActivity(new Intent(getActivity(), ContainerActivity.class));
            }

        });


        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTimer.cancel();
                animatorSet.cancel();
                startActivity(new Intent(getActivity(), ContainerActivity.class));
            }
        });
    }
}