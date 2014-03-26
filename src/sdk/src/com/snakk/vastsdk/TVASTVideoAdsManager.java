package com.snakk.vastsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.snakk.vastsdk.player.TVASTPlayer;
import com.snakk.vastsdk.TVASTAdError.AdErrorCode;
import com.snakk.vastsdk.TVASTAdError.AdErrorType;
import com.snakk.vastsdk.player.TVASTPlayer.TVASTAdPlayerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TVASTVideoAdsManager implements TVASTAdPlayerListener {
    private enum PlaybackState {
        STOPPED, PAUSED, PLAYING;
    }

    public enum TVASTAdEventType {
        CLICK,  //
        COMPLETE,  //
        CREATIVE_VIEW,  //
        IMPRESSION,  //
        CONTENT_PAUSE_REQUESTED,
        CONTENT_RESUME_REQUESTED,
        ERROR,  //
        FIRST_QUARTILE,  //
        MIDPOINT,  //
        MUTE,  //
        UNMUTE,  //
        PAUSE,  //
        START,  //
        THIRD_QUARTILE,  //
        REWIND,  //
        RESUME,  //
        FULLSCREEN, //
        EXPAND,  //
        COLLAPSE,  //
        SKIP,  //
        ACCEPT_INVITATION_LINEAR,
        ACCEPT_INVITATION,
        CLOSE,
        CLOSE_LINEAR;
    }

    private ArrayList<TVASTAdEventListener> mEventListeners;
    private ArrayList<TVASTAdErrorListener> mErrorListeners;
    private TVASTAd mAd;
    private TVASTLinearAd mLinearAd;
    private TVASTPlayer mVideoAdPlayer;
    private TVASTAdsRequest mAdRequest;
    private String mManagerName;
    private boolean mClosingFrameShown;
    private int mVolumeLevel;
    private TVASTAdEventType mAdEventType;
    private PlaybackState mPlayState = PlaybackState.STOPPED;
    private float mPercentPlayed;
    private boolean mIsFullscreen;
    private TVASTAdView mAdView;
    private boolean showCloseButton = true;

    public class TVASTAdEvent {
        public TVASTAdEvent(TVASTAdEventType adEventType) {
            mAdEventType = adEventType;
        }

        public TVASTAdEventType getEventType() {
            return mAdEventType;
        }
    }

    public interface TVASTAdEventListener {
        void onAdEvent(TVASTAdEvent adEvent);
    }

    public TVASTVideoAdsManager(String name, TVASTAdsRequest adRequest, Map<String, List<TVASTAd>> adsMap) {
        mEventListeners = new ArrayList<TVASTAdEventListener>();
        mErrorListeners = new ArrayList<TVASTAdErrorListener>();

        mManagerName = name;
        mAdRequest = adRequest;

        mClosingFrameShown = false;

        // assume the volume is not 0;
        mVolumeLevel = Integer.MAX_VALUE;

        mAd = null;
        List<TVASTAd> ads = adsMap.get("videoAds");
        if (ads != null && ads.size() > 0) {
            mAd = ads.get(0);

            TVASTCreative creative = mAd.getCreatives().get(0);
            mLinearAd = creative.getLinearAd();
        }
    }

    private void sendAdEvent(TVASTAdEvent adEvent) {
        for (TVASTAdEventListener listener : mEventListeners) {
            listener.onAdEvent(adEvent);
        }

        Map<String, String> trackingEvents = mLinearAd.getTrackingEvents();
        String eventName;

        TVASTAdEventType eventType = adEvent.getEventType();
        switch (eventType) {
            case CLICK:
                eventName = "click";
                break;
            case COMPLETE:
                eventName = "complete";
                break;
            case CREATIVE_VIEW:
                eventName = "creativeView";
                break;
            case ERROR:
                eventName = "error";
                break;
            case FIRST_QUARTILE:
                eventName = "firstQuartile";
                break;
            case MIDPOINT:
                eventName = "midpoint";
                break;
            case MUTE:
                eventName = "mute";
                break;
            case UNMUTE:
                eventName = "unmute";
                break;
            case PAUSE:
                eventName = "pause";
                break;
            case START:
                eventName = "start";
                break;
            case THIRD_QUARTILE:
                eventName = "thirdQuartile";
                break;
            case REWIND:
                eventName = "rewind";
                break;
            case RESUME:
                eventName = "resume";
                break;
            case SKIP:
                eventName = "skip";
                break;
            case FULLSCREEN:
                eventName = "fullscreen";
                break;
            case EXPAND:
                eventName = "expand";
                break;
            case COLLAPSE:
                eventName = "collapse";
                break;
            case ACCEPT_INVITATION_LINEAR:
                eventName = "acceptInvitationLinear";
                break;
            case ACCEPT_INVITATION:
                eventName = "acceptInvitation";
                break;
            case CLOSE_LINEAR:
                eventName = "closeLinear";
                sendAdEvent(new TVASTAdEvent(TVASTAdEventType.CONTENT_RESUME_REQUESTED));
                break;
            case CLOSE:
                eventName = "close";
                break;
            default:
                eventName = null;
        }
        if (eventName != null) {
            String postbackUri = trackingEvents.get(eventName);
            doPostback(postbackUri);
            postbackUri = trackingEvents.get("pw-".concat(eventName));
            doPostback(postbackUri);
        }
    }

    public boolean isFullscreen() {
        return mIsFullscreen;
    }

    public void setIsFullscreen(boolean isFullscreen) {
        mIsFullscreen = isFullscreen;
    }

    public TVASTAdView getAdView() {
        return mAdView;
    }

    public void setAdView(TVASTAdView adView) {
        mAdView = adView;
    }

    public void addAdErrorListener(TVASTAdErrorListener errorListener) {
        mErrorListeners.add(errorListener);
    }

    public void addAdEventListener(TVASTAdEventListener eventListener) {
        mEventListeners.add(eventListener);
    }

    public void removeAdErrorListener(TVASTAdErrorListener errorListener) {
        mErrorListeners.remove(errorListener);
    }

    public void removeAdEventListener(TVASTAdEventListener eventListener) {
        mEventListeners.remove(eventListener);
    }

    public void unload() {
        Log.d("SnakkVASTSDK", "Need to remove the manager in this method");

        if (mIsFullscreen) {
            // abort the unload if closing frame not shown
            if (!mClosingFrameShown)
                return;

            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.CLOSE_LINEAR));
        }

        onStop();
        mEventListeners.clear();
        mErrorListeners.clear();
    }

    public boolean play(TVASTPlayer player) {

        this.mVideoAdPlayer = player;

        if (mAd == null)
            return false;

        // register callbacks to be called by the player
        player.addCallback(this);

        mPercentPlayed = 0.0f;

        Log.d("SnakkVASTSDK", "AdsManager playAd()");

        // hide player but still needs to be visible while it's being primed.
//        ViewGroup.LayoutParams lp = ((FrameLayout) player).getLayoutParams();
        LayoutParams lp = ((View) player).getLayoutParams();
        lp.height = 10;
        lp.width = 10;
        ((View) player).setLayoutParams(lp);

        player.getVideoView().setVisibility(View.VISIBLE);

        //TVASTCreative creative = mAd.getCreatives().get(0);
        //mLinearAd = creative.getLinearAd();

        for (String impressionUri : mAd.getImpressions().values()) {
            doPostback(impressionUri);
        }

        if (mAd.getMediaUrl().length() > 0 && mAd.getMediaUrl().startsWith("http"))
            player.playAd(mAd.getMediaUrl());
        else
            player.playAd("http://d2bgg7rjywcwsy.cloudfront.net/videos/15751/113894/encoded/320_480.mp4");

        return true;
    }

    protected void overlayClickableView(final TVASTAdView adView, final String clickUri) {
        // cover screen in transparent button
        final ImageButton transparentButton = new ImageButton(adView.getContext());
        transparentButton.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams buttonLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        transparentButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                adView.removeView(transparentButton);
                //adView.removeAllViews();
                Log.d("SnakkVASTSDK", "Launch webview to show click through, " + clickUri);
                if (clickUri != null && clickUri.length() > 0) {
                    adView.getSettings().setJavaScriptEnabled(true);
                    adView.loadUrl(clickUri);
                }
            }
        });
        adView.addView(transparentButton, buttonLayout);
    }

    public void showCloseButton(boolean isCloseButtonShown) {
        showCloseButton = isCloseButtonShown;
    }

    protected void showInterstitialCloseButton(final TVASTAdView adView) {
        if (!showCloseButton) {
            return;
        }
        final float CLOSE_BUTTON_SIZE_DP = 50.0f;
        final float CLOSE_BUTTON_PADDING_DP = 8.0f;

        StateListDrawable states = new StateListDrawable();
        final Context context = adView.getContext();

        try {
            Drawable d = context.getResources().getDrawable(android.R.drawable.ic_notification_clear_all);
            states.addState(new int[]{-android.R.attr.state_pressed}, d);
        } catch (RuntimeException e) {
            Log.e("SnakkVASTSDK", "Failed to load close button drawable", e);
        }
        ImageButton closeButton = new ImageButton(context);
        closeButton.setImageDrawable(states);
        closeButton.setBackgroundColor(Color.TRANSPARENT);

        closeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                adView.removeAllViews();
                adView.setVisibility(View.GONE);
                sendAdEvent(new TVASTAdEvent(TVASTAdEventType.CLOSE_LINEAR));

                if (mIsFullscreen) {
                    mClosingFrameShown = false;
                    ((Activity) context).finish();
                }
            }
        });

        int xButtonOffset = 45;
        int yButtonOffset = 45;

        //DisplayMetrics metrics = new DisplayMetrics();
        //((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //final float scale = metrics.density;
        final float scale = ((Activity) context).getResources().getDisplayMetrics().density;
        int buttonPadding = (int) (CLOSE_BUTTON_PADDING_DP * scale);
        int buttonSize = (int) (CLOSE_BUTTON_SIZE_DP * scale);
        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(buttonSize, buttonSize);
        //lp.setMargins(xButtonOffset, yButtonOffset, buttonPadding, buttonPadding);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(buttonPadding, 0, buttonPadding, 0);
        adView.addView(closeButton, lp);
    }

    public boolean hasDestinationUrl() {
        String clickThru = mLinearAd.getClickThrough();
        return clickThru != null && clickThru.length() > 0;
    }

    public void loadDestinationUrl(TVASTAdView adView) {
        String clickThru = mLinearAd.getClickThrough();
        if (clickThru != null && clickThru.length() > 0) {
            adView.getSettings().setJavaScriptEnabled(true);
            adView.loadUrl(clickThru);
        }
    }

    public boolean hasClosingFrame() {
        return mAd != null && mLinearAd.getIcons() != null;
    }

    public void showClosingFrame(TVASTAdView adView) {
        if (mAd != null && mLinearAd.getIcons() != null) {
            for (final TVASTLinearIcon icon : mLinearAd.getIcons()) {
                if (icon.getStaticResource() != null) {
                    adView.setAdViewListener(new TVASTAdView.AdViewListener() {
                        @Override
                        public void onLoaded(TVASTAdView adView) {
                            Log.d("SnakkVASTSDK", "Alert Ad Loaded.");
                        }

                        @Override
                        public void onError(TVASTAdView adView, String error) {
                            Log.d("SnakkVASTSDK", "Alert Ad Error.");
                        }

                        @Override
                        public void onClicked(TVASTAdView adView) {
                            Log.d("SnakkVASTSDK", "Alert Ad Clicked.");
                        }

                        @Override
                        public void willLeaveApplication(TVASTAdView adView) {
                            Log.d("SnakkVASTSDK", "Alert Ad Leave.");
                        }
                    });

                    if (Build.VERSION.SDK_INT >= 7) {
                        adView.getSettings().setLoadWithOverviewMode(true);
                    }

                    if ("Application/x-javascript".equalsIgnoreCase(icon.getIconCreativeType())) {
                        adView.getSettings().setJavaScriptEnabled(true);
                        adView.loadHtml("<html><head><script src='" + icon.getStaticResource() + "'></script></head></html>");
                    } else if ("application/x-shockware-flash".equalsIgnoreCase(icon.getIconCreativeType())) {
                        // not supported, do nothing.
                    } else {
                        adView.getSettings().setJavaScriptEnabled(true);
                        String htmlContent = "<html><head><style type='text/css'>html, body { margin: 0; padding: 0; width: 100%; height: 100%; display: table } ";
                        htmlContent += "#content { display: table-cell; margin-left:auto; margin-right:auto; vertical-align: middle; }</style>";
                        htmlContent += "<body><div id='content'><img src='" + icon.getStaticResource() + "' /></div></body></html>";
                        adView.loadHtml(htmlContent);
                    }
                    overlayClickableView(adView, icon.getIconClickThrough());
                    showInterstitialCloseButton(adView);
                    mClosingFrameShown = true;
                    break;
                }
            }
        } else {
            if (mIsFullscreen) {
                // end the activity because there is no closing frame.
                ((Activity) adView.getContext()).finish();
            }
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.CLOSE_LINEAR));
        }
    }

    protected String getErrorURIForErrorCode(int code) {
        String errorUri = mAd.getErrorURI();
        String codeString = String.format(Locale.getDefault(), "%d", code);
        errorUri = errorUri.replace("[ERRORCODE]", codeString).replace("[errorcode]", codeString);

        return errorUri;
    }

    private void onStop() {
        mPlayState = PlaybackState.STOPPED;
        mVideoAdPlayer.stopAd();

        // unregister callback.
        mVideoAdPlayer.removeCallback(this);
    }

    /**
     * VideoAdPlayerCallback implementations follow:
     * @see com.snakk.vastsdk.player.VideoAdPlayer.VideoAdPlayerCallback
     */
    @Override
    public void onVideoClick(TVASTPlayer player) {
        Log.d("SnakkVASTSDK", "Ad Clicked");
        sendAdEvent(new TVASTAdEvent(TVASTAdEventType.CLICK));
        onStop();

        for (TVASTCreative creative : mAd.getCreatives()) {
            TVASTLinearAd linearAd = creative.getLinearAd();

            String clickTrackingUri = linearAd.getClickTracking();
            String clickThroughUri = linearAd.getClickThrough();
            String customClickUri = linearAd.getCustomClick();
            String iconClickThrough = null;
            if (linearAd.getIcons() != null) {
                TVASTLinearIcon icon = linearAd.getIcons().get(0);
                iconClickThrough = icon.getIconClickThrough();
            }

            if (clickThroughUri != null || iconClickThrough != null) {

                String clickThrough = (clickThroughUri != null) ? clickThroughUri : iconClickThrough;
                if (mAdView != null && clickThrough.length() > 0) {
                    mAdView.setVisibility(View.VISIBLE);
                    mAdView.bringToFront();
                    mAdView.getSettings().setJavaScriptEnabled(true);
                    mAdView.loadUrl(clickThrough);
                    showInterstitialCloseButton(mAdView);
                }
            }

            if (clickTrackingUri != null && clickTrackingUri.length() > 0) {
                doPostback(clickTrackingUri);
            }

            if (customClickUri != null && customClickUri.length() > 0) {
                doPostback(customClickUri);
            }
        }
    }

    @Override
    public void onVideoComplete(TVASTPlayer player) {
        Log.d("SnakkVASTSDK", "Ad Ended");
        sendAdEvent(new TVASTAdEvent(TVASTAdEventType.COMPLETE));
        onStop();
    }

    @Override
    public void onVideoError(TVASTPlayer player) {
        Log.d("SnakkVASTSDK", "Ad Error");

        sendAdEvent(new TVASTAdEvent(TVASTAdEventType.ERROR));
        String errorUri = getErrorURIForErrorCode(300);
        doPostback(errorUri);

        onStop();
    }

    @Override
    public void onVideoPause(TVASTPlayer player) {
        Log.d("SnakkVASTSDK", "Ad Pause");
        sendAdEvent(new TVASTAdEvent(TVASTAdEventType.PAUSE));

        mPlayState = PlaybackState.PAUSED;
    }

    @Override
    public void onVideoResume(TVASTPlayer player) {
        if (mPlayState == PlaybackState.STOPPED) {
            Log.d("SnakkVASTSDK", "Ad Start");
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.START));
        } else {
            Log.d("SnakkVASTSDK", "Ad Resume");
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.RESUME));
        }

        mPlayState = PlaybackState.PLAYING;
    }

    @Override
    public void onVideoPlay(TVASTPlayer player) {

        LayoutParams lp = ((View) mVideoAdPlayer).getLayoutParams();
        lp.height = LayoutParams.MATCH_PARENT;
        lp.width = LayoutParams.MATCH_PARENT;
//        lp.height = LayoutParams.WRAP_CONTENT;
//        lp.width = LayoutParams.WRAP_CONTENT;
        ((View) mVideoAdPlayer).setLayoutParams(lp);

        // request for the content to pause
        sendAdEvent(new TVASTAdEvent(TVASTAdEventType.CONTENT_PAUSE_REQUESTED));

        Log.d("SnakkVASTSDK", "Ad Started");
        sendAdEvent(new TVASTAdEvent(TVASTAdEventType.START));

        mPlayState = PlaybackState.PLAYING;
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void onVideoProgress(TVASTPlayer player, int current, int max) {

        float calcPercent = (float) current / (float) max;
        if (mPercentPlayed < 0.25 && calcPercent >= 0.25) {
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.FIRST_QUARTILE));
            mPercentPlayed = calcPercent;
        } else if (mPercentPlayed < 0.50 && calcPercent >= 0.50) {
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.MIDPOINT));
            mPercentPlayed = calcPercent;
        } else if (mPercentPlayed < 0.75 && calcPercent >= 0.75) {
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.THIRD_QUARTILE));
            mPercentPlayed = calcPercent;
        } else if (mPercentPlayed < 1.0 && calcPercent >= 1.0) {
            Log.d("SnakkVASTSDK", "Progress: 100%.");
            //sendAdEvent(new TVASTAdEvent(TVASTAdEventType.CLOSE_LINEAR));
            mPercentPlayed = calcPercent;
        }
    }

    @Override
    public void onVideoVolumeChanged(TVASTPlayer player, int volume) {
        if (mVolumeLevel == 0 && volume > 0) {
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.UNMUTE));
        } else if (mVolumeLevel > 0 && volume == 0) {
            sendAdEvent(new TVASTAdEvent(TVASTAdEventType.MUTE));
        }
        mVolumeLevel = volume;
    }

    private void doPostback(String uri) {
        final String postbackUri = uri;

        TVASTPostbackTask postbackTask = new TVASTPostbackTask(uri);
        postbackTask.setListener(new TVASTPostbackTask.TVASTPostbackListener() {
            @Override
            public void onSuccess(String data) {
                Log.d("SnakkVASTSDK", "Postback:" + postbackUri + " successful.");
            }

            @Override
            public void onFailure(Exception error) {
                Log.d("SnakkVASTSDK", "Postback:" + postbackUri + " failed.");
                TVASTAdError adError = new TVASTAdError(AdErrorType.PLAY, AdErrorCode.VAST_INVALID_URL, error.getMessage());
                TVASTAdErrorEvent adErrorEvent = new TVASTAdErrorEvent(adError);
                for (TVASTAdErrorListener listener : mErrorListeners) {
                    listener.onAdError(adErrorEvent);
                }
            }
        });
        postbackTask.execute(0);
    }
}
