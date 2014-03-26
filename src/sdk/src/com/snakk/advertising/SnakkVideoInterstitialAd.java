package com.snakk.advertising;

/**
 * Video interstitials are full screen ads that display a video ad in their own Activity.
 *
 * Video interstitials are best used at discrete stopping points in your app's flow,
 * such as at the end of a game level, or when the player dies.
 *
 * <h3>Simple example:</h3>
 * <pre>
 * VideoInterstitialAd videoInterstitialAd = SnakkAdvertising.getVideoInterstitialAdForZone(context, "YOUR_ZONE_ID");
 * videoInterstitialAd.show(); // video interstitial is loaded and shown asynchronously
 * </pre>
 *
 * Instances of VideoInterstitialAdImpl are not safe for use by multiple threads.
 */
public interface SnakkVideoInterstitialAd {
    /**
     * Implement this interface to be notified of video interstitial ad lifecycle changes.
     */
    public interface SnakkVideoInterstitialAdListener {
        /**
         * This event is fired once video interstitial content is fully downloaded
         * and is ready to be displayed.
         *
         * @param videoInterstitialAd the video interstitial that caused the event
         */
        public void videoInterstitialDidLoad(SnakkVideoInterstitialAd videoInterstitialAd);

        /**
         * This event is fired once video interstitial closes,
         * and your application is back in focus.
         *
         * @param videoInterstitialAd the video interstitial that caused the event
         */
        public void videoInterstitialDidClose(SnakkVideoInterstitialAd videoInterstitialAd);

        /**
         * This event is fired if the video interstitial request fails to return
         * an ad.
         *
         * @param videoInterstitialAd the video interstitial that caused the event
         * @param error description of the error, used for debugging purposes.
         *        error description's are generally not shown to the end user.
         */
        public void videoInterstitialDidFail(SnakkVideoInterstitialAd videoInterstitialAd, String error);

        /**
         * This event is fired just before the app will be sent to the
         * background.
         *
         * @param videoInterstitialAd the video interstitial that caused the event
         */
        public void videoInterstitialActionWillLeaveApplication(SnakkVideoInterstitialAd videoInterstitialAd);
    }

    /**
     * Used to determine if video interstitial is ready to be shown.
     * @return true if ready for display, false otherwise
     */
    public boolean isLoaded();

    /**
     * Fire off an asynchronous request to server for a video interstitial ad.
     * Use {@link #isLoaded()} or {@link SnakkVideoInterstitialAdListener} to determine
     * when video interstitial is loaded.
     */
    public void load();

    /**
     * Display video interstitial to user.  If video interstitial is not loaded, {@link #load()}
     * will be called and the video interstitial will be loaded and shown asynchronously.
     * Use {@link #isLoaded()} or {@link SnakkVideoInterstitialAdListener} to determine when
     * video interstitial is loaded.
     */
    public void show();

    /**
     * @param videoInterstitialListener the listener instance that will be notified
     * of ad lifecycle events
     */
    public void setListener(final SnakkVideoInterstitialAdListener videoInterstitialListener);

    public SnakkVideoInterstitialAdListener getListener();
}
