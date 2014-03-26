package com.snakk.advertising.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import com.snakk.advertising.*;
import com.snakk.core.SnakkLog;
import com.snakk.vastsdk.*;
import com.snakk.vastsdk.player.TVASTPlayer;


public class VideoInterstitialAdImpl extends AbstractStatefulAd implements SnakkVideoInterstitialAd, TVASTAdsLoader.TVASTAdsLoadedListener, TVASTAdErrorListener {

    private static final String TAG = "Snakk";

    private final Context context;
    private final SnakkAdRequest adRequest;
    private final TVASTAdsLoader videoLoader;
    private SnakkVideoInterstitialAdListener listener = null;
    private TVASTVideoAdsManager videoAdsManager = null;
    private DisplayMetrics metrics = null;


    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Video Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return An interstital ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static SnakkVideoInterstitialAd getVideoInterstitialAdForZone(Context context, String zone) {
        SnakkAdRequest request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        return VideoInterstitialAdImpl.getVideoInterstitialAd(context, request);

    }

    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return An interstital ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static SnakkVideoInterstitialAd getVideoInterstitialAd(Context context, SnakkAdRequest request) {
        return new VideoInterstitialAdImpl(context, request);
    }


    private VideoInterstitialAdImpl(Context context, SnakkAdRequest request) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }

        if (request == null) {
            throw new NullPointerException("Ad request cannot be null");
        }

        this.context = context;
        adRequest = request;
        metrics = this.context.getResources().getDisplayMetrics();

        videoLoader = new TVASTAdsLoader(context);
    }


    public VideoInterstitialAdImpl.SnakkVideoInterstitialAdListener getListener() {
        return listener;
    }

    public void setListener(final VideoInterstitialAdImpl.SnakkVideoInterstitialAdListener videoInterstitialListener) {
        this.listener = videoInterstitialListener;
    }

    public void doLoad() {
        //TODO delay showing until video is primed
        //TODO fire SnakkVideoInterstitialAdListener callbacks

        TVASTAdsRequest tvastRequest = AdRequestImpl.asTVASTImplAdRequest(adRequest);
        tvastRequest.initDefaultParameters(context);

        videoLoader.addAdsLoadedListener(this);
        videoLoader.addAdErrorListener(this);

        videoLoader.requestAds(tvastRequest);
    }

    public void doShow() {
//        SnakkLog.d(TAG, "videoImpl.show");

        AdActivityContentWrapper wrapper = new AdActivityContentWrapper() {

            private RelativeLayout layout = null;
            private VastPlayerView videoView = null;
            private TVASTAdView staticAdView = null;
            // stub code to enable forcing user to watch entire video
            private boolean canCloseVideo = true; // set to false to force user to watch entire video

            @Override
            public View getContentView(final SnakkAdActivity activity) {
                activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
                activity.getRequestedOrientation();
                activity.setCloseButtonVisible(canCloseVideo);
                activity.enableSystemUIAutoDimming();

                staticAdView = getAdView(activity);
                videoView = getVideoView(activity);

                layout = new RelativeLayout(activity);
                layout.addView(staticAdView);
                layout.addView(videoView);

                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
//                        SnakkLog.d(TAG, "AdActivityContentWrapper -> videoView -> layout.onTouch");
                        showAdClickDestination();

                        return true;
                    }
                });

                return layout;
            }

            private void showClosingFrame(SnakkAdActivity activity) {
                layout.setOnClickListener(null);
                if (videoAdsManager.hasClosingFrame()) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) staticAdView.getLayoutParams();
                    lp.width = (int)(320 * metrics.density + 0.5);
                    lp.height = (int)(480 * metrics.density + 0.5);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
                    staticAdView.setLayoutParams(lp);
                    staticAdView.bringToFront();
                    staticAdView.requestLayout();
                    videoAdsManager.showClosingFrame(staticAdView);

                    // force orientation switch if img would overflow screen
                    int minHeight = Math.min(metrics.heightPixels, metrics.widthPixels);
                    if (minHeight < lp.height) {
                        // set to the orientation w/ the largest height
                        if (Build.VERSION.SDK_INT >= 18) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                        }
                        else {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    }
                    staticAdView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                }
                else {
                    activity.close();
                }
            }

            private void showAdClickDestination() {
                layout.setOnClickListener(null);
                if (videoAdsManager.hasDestinationUrl()) {
                    videoView.stopAd();
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) staticAdView.getLayoutParams();
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    staticAdView.setLayoutParams(lp);
                    staticAdView.requestLayout();
                    videoAdsManager.loadDestinationUrl(staticAdView);
                    staticAdView.bringToFront();
                    staticAdView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                }
            }

            private TVASTAdView getAdView(final SnakkAdActivity activity) {
                TVASTAdView adView = new TVASTAdView(activity);
                adView.setAdViewListener(new TVASTAdView.AdViewListener() {
                    @Override
                    public void onLoaded(TVASTAdView adView) {
                        // noop
                    }

                    @Override
                    public void onError(TVASTAdView adView, String error) {
                        // noop
                    }

                    @Override
                    public void onClicked(TVASTAdView adview) {
                        // noop
                    }

                    @Override
                    public void willLeaveApplication(TVASTAdView adview) {
                        if (listener != null) {
                            listener.videoInterstitialActionWillLeaveApplication(VideoInterstitialAdImpl.this);
                        }
                    }
                });
                adView.setVisibility(View.GONE);
                return adView;
            }

            private VastPlayerView getVideoView(final SnakkAdActivity activity) {
                videoView = new VastPlayerView(context);
                videoView.addCallback(new TVASTPlayer.TVASTAdPlayerListener() {
                    @Override
                    public void onVideoClick(TVASTPlayer player) {
                        SnakkLog.d(TAG, "onVideoClick");
                    }

                    @Override
                    public void onVideoComplete(TVASTPlayer player) {
                        SnakkLog.d(TAG, "Video Ad Complete!");
                        canCloseVideo = true;
                        activity.setCloseButtonVisible(true);

                        showClosingFrame(activity);
                        //TODO auto close interstitial if no closing frame ad
                    }

                    @Override
                    public void onVideoError(TVASTPlayer player) {
                        if (listener != null) {
                            //TODO pass a more useful message?
                            String msg = "Failure during video playback";
                            listener.videoInterstitialDidFail(VideoInterstitialAdImpl.this, msg);
                        }
                    }

                    @Override
                    public void onVideoPause(TVASTPlayer player) {
                        // noop
                    }

                    @Override
                    public void onVideoPlay(TVASTPlayer player) {
                        // noop
                    }

                    @Override
                    public void onVideoProgress(TVASTPlayer player, int current, int max) {
                        // noop
//                        SnakkLog.d(TAG, "onVideoProgress(" + current + ", " + max + ")");
                    }

                    @Override
                    public void onVideoResume(TVASTPlayer player) {
                        // noop
                    }

                    @Override
                    public void onVideoVolumeChanged(TVASTPlayer player, int volume) {
                        // noop
                    }
                });

                return videoView;
            }

            @Override
            public void done() {
                SnakkLog.d(TAG, "done called!");
                if (listener != null) {
                    listener.videoInterstitialDidClose(VideoInterstitialAdImpl.this);
                }
            }

            @Override
            public boolean shouldClose() {
                SnakkLog.d(TAG, "shouldClose Called!");
                return canCloseVideo;
            }

            @Override
            public void startContent() {
                SnakkLog.d(TAG, "startContent");
                videoAdsManager.play(videoView);
            }

            @Override
            public void stopContent() {
                SnakkLog.d(TAG, "stopContent");
            }
        };
        Intent i = new Intent(context, SnakkAdActivity.class);
        Parcelable wrapperSharable = new Sharable<AdActivityContentWrapper>(wrapper, SnakkAdActivity.CONTENT_WRAPPER_EXTRA);
        i.putExtra(SnakkAdActivity.CONTENT_WRAPPER_EXTRA, wrapperSharable);
        context.startActivity(i);
    }

    @Override
    public void onAdError(TVASTAdErrorEvent adErrorEvent) {
        SnakkLog.d(TAG, "Ad error: " + adErrorEvent);
        videoLoader.removeAdErrorListener(this);
        videoLoader.removeAdsLoadedListener(this);

        if (listener != null) {
            listener.videoInterstitialDidFail(this, adErrorEvent.getError().getMessage());
        }
    }

    @Override
    public void onAdsLoaded(TVASTAdsLoader.TVASTAdsLoadedEvent event) {
        SnakkLog.d(TAG, "Ad Loaded: " + event);
        videoLoader.removeAdErrorListener(this);
        videoLoader.removeAdsLoadedListener(this);

        videoAdsManager = event.getManager();
        videoAdsManager.addAdEventListener(new TVASTVideoAdsManager.TVASTAdEventListener() {
            @Override
            public void onAdEvent(TVASTVideoAdsManager.TVASTAdEvent adEvent) {
                SnakkLog.d(TAG, "videoAdsManager.onAdEvent: " + adEvent.getEventType());
            }
        });
        videoAdsManager.showCloseButton(false);
        if (ratchetState(State.LOADED)) {
            if (showImmediately) {
                show();
            }
        }

        if (listener != null) {
            listener.videoInterstitialDidLoad(this);
        }
    }
}
