package com.snakk.advertising.internal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.snakk.core.SnakkLog;

import static android.R.drawable.btn_dialog;

/**
 * wrapper view for interstitial ads, which handles displaying a close button.
 */
public class InterstitialBaseView extends FrameLayout {
    private static final String TAG = "Snakk";

    private DisplayMetrics metrics;
    private View closeButton;

    public InterstitialBaseView(Context context) {
        super(context);
        setup();
    }

    public InterstitialBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public InterstitialBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        metrics = getResources().getDisplayMetrics();
        buildCloseButton();
        setBackgroundColor(Color.BLACK);
    }

    private static void beforeAdView(View child) {
        ViewParent parent = child.getParent();
        if (parent != null) {
//            SnakkLog.w(TAG, "Content view was previously attached elsewhere... re-parenting...");
            Log.w(TAG, "Content view was previously attached elsewhere... re-parenting...");
            ((ViewGroup)parent).removeView(child);
        }
    }

    @Override
    public void addView(View child) {
        beforeAdView(child);
        super.addView(child);
        afterAdView();
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        beforeAdView(child);
        super.addView(child, params);
        afterAdView();
    }


    @Override
    public void addView(View child, int index) {
        beforeAdView(child);
        super.addView(child, index);
        afterAdView();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        beforeAdView(child);
        super.addView(child, index, params);
        afterAdView();
    }

    @Override
    public void addView(View child, int width, int height) {
        beforeAdView(child);
        super.addView(child, width, height);
        afterAdView();
    }

    private void afterAdView() {
        // ensure that close button is on top of view hierarchy
        if (closeButton != null) {
            closeButton.bringToFront();
        }
    }

    private void buildCloseButton() {
        StateListDrawable states = new StateListDrawable();

        try {
            Drawable d = getResources().getDrawable(btn_dialog);
//            Drawable d = getResources().getDrawable(ic_notification_clear_all);
            states.addState(new int[]{-android.R.attr.state_pressed}, d);
        } catch (RuntimeException e){
            SnakkLog.e(TAG, "Failed to load close button drawable: " + e);
        }
        ImageButton closeButton = new ImageButton(getContext());
        closeButton.setImageDrawable(states);
        closeButton.setBackgroundColor(Color.TRANSPARENT);

//        closeButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                finish();
//            }
//        });

        final float scale = metrics.density;
        int buttonPadding = (int) (0.0 * scale);
        int buttonSize = (int) (50.0 * scale);
//        closeButton.setBackgroundColor(Color.RED);
        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(buttonSize, buttonSize);

        lp.gravity = Gravity.TOP | Gravity.RIGHT;
//        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(10, 10, buttonPadding, buttonPadding);

        addView(closeButton, lp);
        this.closeButton = closeButton;
    }

    public void setCloseButtonVisible(boolean isVisible) {
        closeButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setCloseButtonOnClickListener(OnClickListener listener) {
        closeButton.setOnClickListener(listener);
    }
}
