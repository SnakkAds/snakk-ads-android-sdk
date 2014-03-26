package com.snakk.advertising;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;
import com.snakk.advertising.*;
import com.snakk.advertising.internal.*;
import com.snakk.adview.AdViewCore;
import com.snakk.adview.Utils;

public final class SnakkAdvertising {

    private static SnakkAdvertising instance = null;
    private static final Object lock = new Object();

    private SnakkAdvertising() {}

    public static String getVersion() {
        return AdViewCore.VERSION;
    }

    /**
     * Get an instance of the SnakkAdvertising which allows you to instantiate
     * ad objects
     * @return a SnakkAdvertising instance
     */
    public static SnakkAdvertising get() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SnakkAdvertising();
                }
            }
        }
        return instance;
    }

    /**
     * Use this utility method to test if you've configured your AndroidManifest.xml
     * properly.
     * @param context your application's context
     * @throws IllegalStateException if your manifest is not configured properly.
     *         The exception description explains what is mis-configured.
     */
    public void validateSetup(Context context) {
        // test that required permissions are specified
        if (!Utils.hasPermission(context, Manifest.permission.INTERNET)) {
            throw new IllegalStateException("SnakkAdvertising requires the \"INTERNET\" permission.");
        }

        // test that SnakkAdActivity is registered
        PackageManager pm = context.getPackageManager();
        ComponentName cn = new ComponentName(context.getPackageName(), SnakkAdActivity.class.getCanonicalName());
        try {
            pm.getActivityInfo(cn, PackageManager.GET_META_DATA);
            Toast.makeText(context, "SnakkAdvertising module was set up properly!", Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("SnakkAdvertising requires the \"SnakkAdActivity\" activity.");
        }

    }


    /*******************************************************
     * SnakkAdRequest factory methods
     *******************************************************/


    /**
     * Factory method which generates AdRequest objects used to hold request configuration details.
     * @param zone Identifier of ad placement to be loaded.
     * @return A SnakkAdRequest instance that can be used to initialize an ad
     */
    public SnakkAdRequest getAdRequestForZone(String zone) {
        return getAdRequestBuilder(zone).getPwAdRequest();
    }

    /**
     * Factory method which generates AdRequest builder objects used to construct custom request objects.
     * @param zone Identifier of ad placement to be loaded.
     * @return A SnakkAdRequestBuilder instance that can be used to construct a SnakkAdRequest
     */
    public SnakkAdRequest.Builder getAdRequestBuilder(String zone) {
        return new AdRequestImpl.BuilderImpl(zone);
    }


    /*******************************************************
     * SnakkAdPrompt factory methods
     *******************************************************/


    /**
     * Factory method which generates AdPrompt object using zone.
     * @param context Application context that will be used to show Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return An AdPrompt object that is ready to be loaded.
     *         Call {@link com.snakk.advertising.SnakkAdPrompt#load()} to initiate ad request.
     * @see com.snakk.advertising.SnakkAdPrompt
     */
    public SnakkAdPrompt getAdPromptForZone(Context context, String zone) {
        return AdPromptImpl.getAdPromptForZone(context, zone);
    }

    /**
     * Factory method which generates AdPrompt object using SnakkAdRequest.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return An AdPrompt object that is ready to be loaded.
     *         Call {@link SnakkAdPrompt#load()} to initiate ad request.
     * @see SnakkAdPrompt
     */
    public SnakkAdPrompt getAdPrompt(Context context, SnakkAdRequest request) {
        return AdPromptImpl.getAdPrompt(context, request);
    }

    /*******************************************************
     * SnakkBannerAdView factory methods
     *******************************************************/


    /**
     * Factory method which generates Banner ad object.
     * {@link com.snakk.advertising.SnakkBannerAdView#getBannerAd(android.content.Context)}
     * @see com.snakk.advertising.SnakkBannerAdView
     */
    public SnakkBannerAdView getAdBannerView(Context context) {
        return SnakkBannerAdView.getBannerAd(context);
    }


    /*******************************************************
     * SnakkInterstitialAd factory methods
     *******************************************************/


    /**
     * Factory method which generates Interstitial Ad object.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return An interstital ad object that is ready to be loaded.
     *         Call {@link com.snakk.advertising.SnakkInterstitialAd#load()} to initiate ad request.
     * @see com.snakk.advertising.SnakkInterstitialAd
     */
    public SnakkInterstitialAd getInterstitialAd(Context context, SnakkAdRequest request) {
        return InterstitialAdImpl.getInterstitialAd(context, request);
    }

    /**
     * Factory method which generates Interstitial Ad object
     * @param context Application context that will be used to show Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return An interstital ad object that is ready to be loaded.
     *         Call {@link SnakkInterstitialAd#load()} to initiate ad request.
     * @see SnakkInterstitialAd
     */
    public SnakkInterstitialAd getInterstitialAdForZone(Context context, String zone) {
        return InterstitialAdImpl.getInterstitialAdForZone(context, zone);
    }


    /*******************************************************
     * SnakkVideoInterstitialAd factory methods
     *******************************************************/


    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Interstial Ad
     * @param request Request object used to hold request configuration details.
     * @return A video interstital ad object that is ready to be loaded.
     *         Call {@link SnakkVideoInterstitialAd#load()} to initiate ad request.
     * @see SnakkVideoInterstitialAd
     */
    public SnakkVideoInterstitialAd getVideoInterstitialAd(Context context, SnakkAdRequest request) {
        return VideoInterstitialAdImpl.getVideoInterstitialAd(context, request);
    }

    /**
     * Factory method which generates Video Interstitial Ad objects.
     * @param context Application context that will be used to show Video Interstial Ad
     * @param zone Identifier of ad placement to be loaded.
     * @return A video interstital ad object that is ready to be loaded.
     *         Call {@link SnakkVideoInterstitialAd#load()} to initiate ad request.
     * @see SnakkVideoInterstitialAd
     */
    public SnakkVideoInterstitialAd getVideoInterstitialAdForZone(Context context, String zone) {
        return VideoInterstitialAdImpl.getVideoInterstitialAdForZone(context, zone);
    }

//TODO document how to capture google play referral codes
//    /**
//     * Track application install the first time it is loaded.
//     * @param context application content
//     * @throws NullPointerException if context is null
//     */
//    public void trackInstall(Context context) {
//        trackInstall(context, null);
//    }
//
//    /**
//     * Track application install the first time it is loaded.
//     * @param context application context
//     * @param offer the offer for which this install can be attributed to
//     * @throws NullPointerException if context is null
//     */
//    public void trackInstall(Context context, String offer) {
//        //TODO implement me!
//    }
//
//    @Override
//    protected void onActivityStart(Activity activity) {
//        super.onActivityStart(activity);
//        if (BuildConfig.DEBUG) {
//            validateSetup(activity);
//        }
//    }
}
