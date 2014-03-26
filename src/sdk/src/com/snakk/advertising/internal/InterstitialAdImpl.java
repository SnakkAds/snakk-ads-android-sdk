package com.snakk.advertising.internal;

import android.content.Context;
import com.snakk.advertising.SnakkAdRequest;
import com.snakk.advertising.SnakkInterstitialAd;
import com.snakk.adview.AdInterstitialView;
import com.snakk.adview.AdViewCore;

public final class InterstitialAdImpl extends AbstractStatefulAd implements SnakkInterstitialAd, AdViewCore.OnInterstitialAdDownload {

    private final AdInterstitialView legacyInterstitialAd;
    private SnakkInterstitialAd.SnakkInterstitialAdListener listener = null;


    /**
     * Factory method which generates Interstitial Ad object
     * @param context Application context that will be used to show Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return An interstital ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static SnakkInterstitialAd getInterstitialAdForZone(Context context, String zone) {
        SnakkAdRequest request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        return InterstitialAdImpl.getInterstitialAd(context, request);

    }

    /**
     * Factory method which generates Interstitial Ad object.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return An interstital ad object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static SnakkInterstitialAd getInterstitialAd(Context context, SnakkAdRequest request) {
        return new InterstitialAdImpl(context, request);
    }


    private InterstitialAdImpl(Context context, SnakkAdRequest request) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }

        if (request == null) {
            throw new NullPointerException("Ad request cannot be null");
        }

        legacyInterstitialAd = new AdInterstitialView(context, AdRequestImpl.asImplAdRequest(request));
    }


    public SnakkInterstitialAd.SnakkInterstitialAdListener getListener() {
        return listener;
    }

    /**
     * @param interstitialListener the listener instance that will be notified
     * of ad lifecycle events
     */
    public void setListener(final SnakkInterstitialAd.SnakkInterstitialAdListener interstitialListener) {
        this.listener = interstitialListener;
    }

    /**
     * Fire off an asynchronous request to server for an interstitial ad.
     * Use {@link #isLoaded()} or {@link SnakkInterstitialAdListener} to determine
     * when interstitial is loaded.
     */
    public void doLoad() {
        legacyInterstitialAd.setOnInterstitialAdDownload(this);
        legacyInterstitialAd.load();
    }

    /**
     * Display interstitial to user.  If interstitial is not loaded, {@link #load()}
     * will be called and the interstitial will be loaded and shown asynchronously.
     * Use {@link #isLoaded()} or {@link SnakkInterstitialAdListener} to determine when
     * interstitial is loaded.
     */
    public void doShow() {
        legacyInterstitialAd.showInterstitial();
    }




    @Override
    public void willLoad(AdViewCore adView) {
        // noop
    }

    @Override
    public void ready(AdViewCore adView) {
        if (ratchetState(State.LOADED)) {
            if (listener != null) {
                listener.interstitialDidLoad(InterstitialAdImpl.this);
            }

            if (showImmediately) {
                show();
            }
        }
    }

    @Override
    public void willOpen(AdViewCore adView) {
        // noop
    }

    @Override
    public void didClose(AdViewCore adView) {
        if (listener != null) {
            listener.interstitialDidClose(InterstitialAdImpl.this);
        }
        legacyInterstitialAd.destroy();
    }

    @Override
    public void error(AdViewCore adView, String error) {
        if (listener != null) {
            listener.interstitialDidFail(InterstitialAdImpl.this, error);
        }
        ratchetState(State.DONE);
    }

    @Override
    public void clicked(AdViewCore adView) {
        // noop
    }

    @Override
    public void willLeaveApplication(AdViewCore adView) {
        if (listener != null) {
            listener.interstitialActionWillLeaveApplication(InterstitialAdImpl.this);
        }
    }
}
