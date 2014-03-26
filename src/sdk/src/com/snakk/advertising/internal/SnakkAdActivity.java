package com.snakk.advertising.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class SnakkAdActivity extends Activity {
    private static final String TAG = "Snakk";
    public static final String CONTENT_WRAPPER_EXTRA = "AdActivityContentWrapper";

    private InterstitialBaseView contentView = null;
    private AdActivityContentWrapper adActivityContentWrapper = null;
    private boolean isContentStarted = false;

    /**
     * Convenience method for firing off the SnakkAdActivity
     * @param context the calling context
     * @param wrapper the wrapper used to pass data to the activity
     */
    public static void startActivity(Context context, AdActivityContentWrapper wrapper) {
        startActivity(context, wrapper, null);
    }

    /**
     * Convenience method for firing off the SnakkAdActivity
     * @param context the calling context
     * @param wrapper the wrapper used to pass data to the activity
     */
    public static void startActivity(Context context, AdActivityContentWrapper wrapper,
                                     Bundle extras) {
        Intent i = new Intent(context, SnakkAdActivity.class);
        if (extras != null) {
            i.putExtras(extras);
        }
        Parcelable wrapperSharable = new Sharable<AdActivityContentWrapper>(
                wrapper, SnakkAdActivity.CONTENT_WRAPPER_EXTRA);
        i.putExtra(SnakkAdActivity.CONTENT_WRAPPER_EXTRA, wrapperSharable);
        context.startActivity(i);
    }

    /**
     * Used to programmatically close SnakkAdActivity... example usage includes a close
     * button, or when a video ad finishes playing.
     */
    public void close() {
        adActivityContentWrapper.stopContent();
        finish();
        adActivityContentWrapper.done();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        SnakkLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // defaults, override in AdActivityContentWrapper.getContentView(...)
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        Bundle extras = getIntent().getExtras();
        Sharable<AdActivityContentWrapper> wrapperSharable = extras.getParcelable(CONTENT_WRAPPER_EXTRA);
        adActivityContentWrapper = wrapperSharable.obj();

        contentView = new InterstitialBaseView(this);
        contentView.setCloseButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adActivityContentWrapper.shouldClose()) {
                    close();
                }
            }
        });

        View wrapperView = adActivityContentWrapper.getContentView(this);
        ViewGroup.LayoutParams lp = adActivityContentWrapper.getContentLayoutParams();
        contentView.addView(wrapperView, lp);

        setContentView(contentView);
    }

    public void setCloseButtonVisible(boolean isVisible) {
        contentView.setCloseButtonVisible(isVisible);
    }

    @Override
    public void onBackPressed() {
        // don't call super, we'll handle in close(), if allowed
//        SnakkLog.d(TAG, "SnakkAdActivity.onBackPressed");
//        Log.d(TAG, "SnakkAdActivity.onBackPressed");
        if (adActivityContentWrapper.shouldClose()) {
            close();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        SnakkLog.d(TAG, "onDetachedFromWindow");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isContentStarted) {
            adActivityContentWrapper.startContent();
            isContentStarted = true;
        }
    }

    //TODO handle optional closing of this activity when user multitasks?
//    @Override
//    protected void onPause() {
//        super.onPause();
//        SnakkLog.d(TAG, "onPause");
//    }

    public void enableSystemUIAutoDimming() {
        if (Build.VERSION.SDK_INT >= 11) {
            // dim the lights on newer devices
            final Handler handler = new Handler(Looper.getMainLooper());
            final Runnable dimmingRunnable = new Runnable(){
                @Override
                public void run() {
//                    Log.d("SendDROID", "dimming");
                    SnakkAdActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            };
            handler.post(dimmingRunnable);
            // re-dim a short time after user interacts w/ system UI
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
//                    Log.d("SendDROID", "will dim in a moment...");
                    if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                        handler.postDelayed(dimmingRunnable, 2000);
                    }
                }
            });
        }
    }

    /**
     * if ad interaction causes external activity to be loaded (such as google play),
     * this is called after that activity completes... (e.g. user hits back).
     *
     * automatically close this SnakkAdActivity, returning control back to the
     * underlying app
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");
        close();
    }
}
