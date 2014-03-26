package com.snakk.advertising;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.snakk.advertising.internal.AdRequestImpl;
import com.snakk.adview.AdRequest;
import com.snakk.adview.AdView;
import com.snakk.adview.AdViewCore;
import com.snakk.adview.Utils;

/**
 * Banners are ads that are shown within the layout of your app.
 *
 * <h3>XML only implementation example:</h3>
 * <pre>
 * </pre>
 *
 * <h3>Simple code implementation example:</h3>
 * <pre>
 *     SnakkBannerAdView bannerView = SnakkBannerAdView.getBannerAd(context);
 *
 *     // add bannerView to activity layout...
 *
 *     bannerView.startRequestingAdsForZone("YOUR_ZONE_ID");
 * </pre>
 *
 * Instances of SnakkBannerAdView are not safe for use by multiple threads.
 */
//TODO should View be split out into a separate class so we can use a static factory?
public final class SnakkBannerAdView extends ViewGroup {

    /**
     * Implement this interface to be notified of banner ad lifecycle changes.
     */
    public interface BannerAdListener {

        /**
         * An ad was received and displayed.
         * @param bannerAd the banner ad that loaded
         */
        public void onReceiveBannerAd(SnakkBannerAdView bannerAd);

        /**
         * An error occurred while requesting an ad.  Possible errors include network
         * failure or no ad inventory to display for this session.
         * @param bannerAd the banner ad which experienced an error
         * @param errorMsg description of the error that occurred.  Error messages are
         * informational and are not generally displayed to the user.
         */
        public void onBannerAdError(SnakkBannerAdView bannerAd, String errorMsg);

        /**
         * Called just before an ad will go full-screen, covering up your app.
         * @param bannerAd the banner ad that is about to cover up your app
         */
        public void onBannerAdFullscreen(SnakkBannerAdView bannerAd);

        /**
         * Called once a full screen ad has closed, leaving your app in the foreground
         * @param bannerAd the banner ad that just returned control to your app
         */
        public void onBannerAdDismissFullscreen(SnakkBannerAdView bannerAd);

        /**
         * Called just before an ad interaction will start a new app, sending yours to the background.
         * @param bannerAd the banner ad that is causing your app to go into the background
         */
        public void onBannerAdLeaveApplication(SnakkBannerAdView bannerAd);
    }

    public SnakkBannerAdView(Context context) {
        super(context);
    }

    public SnakkBannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs, 0);
    }

    public SnakkBannerAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(attrs, defStyle);
    }

    public static SnakkBannerAdView getBannerAd(Context context) {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }

        return new SnakkBannerAdView(context);
    }

    /**
     * Start requesting ads for the zone provided.
     * @param zone Identifier of ad placement to be loaded.
     */
    public final void startRequestingAdsForZone(String zone) {
//        AdRequestImpl request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        SnakkAdRequest request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        startRequestingAds(request);
    }

    /**
     * Start requesting ads, using the {@link com.snakk.advertising.internal.AdRequestImpl} instance provided.
     * @param request the ad request to be used while making requests
     */
    public final void startRequestingAds(SnakkAdRequest request) {
        if (request == null) {
            throw new NullPointerException("request cannot be null");
        }

        adRequest = request;
        resumeRequestingAds();
    }

    /**
     * Restarts requesting ads from server on the ad update interval.
     * @throws IllegalStateException if a valid {@link com.snakk.advertising.internal.AdRequestImpl}
     * was not provided earlier
     */
    public void resumeRequestingAds() {
        removeAd();
        legacyBannerAdView = new AdView(getContext());
        legacyBannerAdView.setUpdateTime(0); // stop implementation from cycling on it's own
        setupLegacyListener();

        AdRequest legacyAdRequest = AdRequestImpl.asImplAdRequest(adRequest);
        setContainerSize(legacyAdRequest);
        legacyBannerAdView.startRequestingAds(legacyAdRequest);

        // schedule the next ad request, unless ad rotation is disabled
        long updateMillis = adUpdateIntervalSeconds * 1000;
        if (updateMillis > 0) {
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, updateMillis);
        }
    }

    /**
     * injects container size into legacy ad request so the server can use it for ad decisioning
     * @param legacyAdRequest the legacy ad request to modify.
     */
    private void setContainerSize(AdRequest legacyAdRequest) {
        LayoutParams lp = getLayoutParams();
        if (lp != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getContext().getSystemService(
                    Context.WINDOW_SERVICE);

            wm.getDefaultDisplay().getMetrics(metrics);
            float mDensity = metrics.density;

            if (lp.width != LayoutParams.MATCH_PARENT
                    && lp.width != LayoutParams.WRAP_CONTENT) {
                legacyAdRequest.setWidth((int)(lp.width / mDensity + 0.5));
            }
            if (lp.width != LayoutParams.MATCH_PARENT
                    && lp.width != LayoutParams.WRAP_CONTENT) {
                legacyAdRequest.setHeight((int)(lp.height / mDensity + 0.5));
            }
        }
    }

    private void removeAd() {
        if (legacyBannerAdView != null) {
            // clean up previous instance
            ViewGroup parent = (ViewGroup)legacyBannerAdView.getParent();
            if (parent != null) {
                parent.removeView(legacyBannerAdView);
            }
            legacyBannerAdView.destroy();
            legacyBannerAdView = null;
        }
    }

    /**
     * stops banner ad from requesting new ads
     */
    public void stopRequestingAds() {
        autoLoad = false;

        // abort the update timer
        timerHandler.removeCallbacks(timerRunnable);
    }

    /**
     * Sets the interval at which a new ad is requested from the server.
     * @param delaySeconds number of seconds to wait between requesting an ad.
     * Set to 0 to disable auto update.
     */
    public void setAdUpdateInterval(int delaySeconds) {
        if (delaySeconds < 0) {
            throw new IllegalArgumentException("delaySeconds cannot be negative");
        }

        adUpdateIntervalSeconds = delaySeconds;

        if (delaySeconds == 0) {
            stopRequestingAds();
        }
    }

    /**
     *
     * @return the current ad update interval, in seconds
     */
    public int getAdUpdateInterval() {
        return adUpdateIntervalSeconds;
    }

    /**
     *
     * @param listener the listener instance that will be notified
     * of ad lifecycle events
     */
    public void setListener(final BannerAdListener listener) {
        this.listener = listener;
    }

    /**
     * wires up the connections between this class and the legacy banner code so that lifecycle
     * events bubble all the way back up.  Ad auto update is canceled when user interacts w/ ad.
     */
    private void setupLegacyListener() {
        legacyBannerAdView.setOnAdDownload(new AdViewCore.OnAdDownload() {
            @Override
            public void begin(AdViewCore adView) {
                // noop
            }

            @Override
            public void end(AdViewCore adView) {
                if (listener != null) {
                    listener.onReceiveBannerAd(SnakkBannerAdView.this);
                }
                addView(adView);
            }

            @Override
            public void error(AdViewCore adView, String error) {
                if (listener != null) {
                    listener.onBannerAdError(SnakkBannerAdView.this, error);
                }
                removeAd();
            }

            @Override
            public void clicked(AdViewCore adView) {
                // noop
            }

            @Override
            public void willPresentFullScreen(AdViewCore adView) {
                stopRequestingAds();
                if (listener != null) {
                    listener.onBannerAdFullscreen(SnakkBannerAdView.this);
                }
            }

            @Override
            public void didPresentFullScreen(AdViewCore adView) {
                // noop
            }

            @Override
            public void willDismissFullScreen(AdViewCore adView) {
                if (listener != null) {
                    listener.onBannerAdDismissFullscreen(SnakkBannerAdView.this);
                }
            }

            @Override
            public void willLeaveApplication(AdViewCore adView) {
                if (listener != null) {
                    listener.onBannerAdLeaveApplication(SnakkBannerAdView.this);
                }
            }

            @Override
            public void didResize(AdViewCore adView) {
                stopRequestingAds();
            }
        });
    }

    /**
     *
     * @return the current BannerAdListener object
     */
    public BannerAdListener getListener() {
        return this.listener;
    }

    /**
     *
     * @return the width, in pixels, of the currently loaded ad,
     * or -1 if no ad is currently available
     */
    public int getCurrentAdWidth() {
        if (legacyBannerAdView == null) {
            return -1;
        }
        return legacyBannerAdView.getAdWidth();
    }

    /**
     *
     * @return the height, in pixels, of the currently loaded ad,
     * or -1 if no ad is currently available
     */
    public int getCurrentAdHeight() {
        if (legacyBannerAdView == null) {
            return -1;
        }
        return legacyBannerAdView.getAdHeight();
    }

    /**
     * Set the location coordinates for geo-targeting.
     * @param latitude the latitude in decimal degrees
     * @param longitude the longitude in decimal degrees
     */
    public void updateLocation(double latitude, double longitude) {
        if (legacyBannerAdView == null) {
            return;
        }
        legacyBannerAdView.setLatitude(String.valueOf(latitude));
        legacyBannerAdView.setLongitude(String.valueOf(longitude));
    }


    /**************************************************************
     * Impl
     **************************************************************/


    static final int REFRESH_DELAY_SECONDS = 60;
    private static final String TAG = "Snakk";

    private AdView legacyBannerAdView = null;
    private BannerAdListener listener = null;
    private SnakkAdRequest adRequest = null;
    private int adUpdateIntervalSeconds = REFRESH_DELAY_SECONDS;

    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            resumeRequestingAds();
        }
    };

    /**
     * Used by xml implementation to determine if ad requests should start
     * immediately after banner view is attached to the view hierarchy.
     */
    private boolean autoLoad = true;

    /**
     * Used to pause banner rotation when app goes into background
     */
    private boolean isAttached = false;

    private void initAttributes(AttributeSet attrs, int defStyle) {
        final String zone = Utils.getStringResource(getContext(), attrs.getAttributeValue(null, "zone"));
        boolean autoLoadDefault = (zone != null);
        autoLoad = attrs.getAttributeBooleanValue(null, "auto_load", autoLoadDefault);
        int adUpdateIntervalSeconds = Utils.getIntegerResource(getContext(),
                attrs.getAttributeValue(null, "update_interval"), REFRESH_DELAY_SECONDS);

        setAdUpdateInterval(adUpdateIntervalSeconds);
        final boolean isTestMode = attrs.getAttributeBooleanValue(null, "test_mode", false);

        if (zone != null) {
            adRequest = new AdRequestImpl.BuilderImpl(zone)
                    .setTestMode(isTestMode)
                    .getPwAdRequest();
        }

        if (autoLoad && zone == null) {
            throw new IllegalStateException("'auto_load' attribute cannot be used without 'zone' attribute.");
        }
    }


    /**************************************************************
     * View overrides
     **************************************************************/


    @Override
    protected void onAttachedToWindow() {
//        SnakkLog.d(TAG, "onAttachedToWindow()");
        isAttached = true;
        if(autoLoad && adRequest != null) {
            // banner was created via layout xml and is set to auto_load
            // start the request loop

            final ViewTreeObserver vto = getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                    SnakkLog.d(TAG, "onGlobalLayout()");
                    vto.removeOnGlobalLayoutListener(this);
                    resumeRequestingAds();

                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
//        SnakkLog.d(TAG, "onVisibilityChanged(" + changedView + ", " + visibility + ")");
        super.onVisibilityChanged(changedView, visibility);
        if (isAttached && visibility != View.VISIBLE) {
            // stop making ad requests when app goes into the background
            stopRequestingAds();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        SnakkLog.d(TAG, "SnakkBannerAdView.onMeasure(...)");

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getMetrics(metrics);
        float mDensity = metrics.density;

        int maxWidth = Math.max(getCurrentAdWidth(), 0);
        int maxHeight = Math.max(getCurrentAdHeight(), 0);
//        SnakkLog.d(TAG, "mw: " + maxWidth + ", mh: " + maxHeight);

        maxWidth = (int)(maxWidth * mDensity + 0.5);
        maxHeight = (int)(maxHeight * mDensity + 0.5);

//        SnakkLog.d(TAG, "mw: " + maxWidth + ", mh: " + maxHeight);

        int resolvedWidth = resolveSize(maxWidth, widthMeasureSpec);
        int resolvedHeight = resolveSize(maxHeight, heightMeasureSpec);

        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (legacyBannerAdView != null) {
            // center the ad inside the container

            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getContext().getSystemService(
                    Context.WINDOW_SERVICE);

            wm.getDefaultDisplay().getMetrics(metrics);
            float mDensity = metrics.density;


            int containerWidth = r - l;
            int containerHeight = b - t;

            int adWidth = getCurrentAdWidth();
            int adHeight = getCurrentAdHeight();

            if (adWidth <= 0 || adHeight <= 0) {
                // server didn't specify an ad size, just default to container size
                adWidth = containerWidth;
                adHeight = containerHeight;
            }
            else {
                adWidth = (int)(adWidth * mDensity + 0.5);
                adHeight = (int)(adHeight * mDensity + 0.5);
            }

            int adLeft = (containerWidth - adWidth) / 2;
            int adTop = (containerHeight - adHeight) / 2;
            int adRight = adWidth + adLeft;
            int adBottom = adHeight + adTop;

            legacyBannerAdView.layout(adLeft, adTop, adRight, adBottom);
        }
    }
}
