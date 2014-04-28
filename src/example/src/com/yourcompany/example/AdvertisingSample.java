package com.yourcompany.example;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import com.snakk.advertising.*;
import com.snakk.advertising.internal.DeviceCapabilities;

import java.util.HashMap;
import java.util.Map;

public class AdvertisingSample extends Activity {

    private final static String TAG = "Snakk";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // test that you've integrated properly
        // NOTE: remove this before your app goes live!
        SnakkAdvertising.get().validateSetup(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    public void simpleAdPromptExmple() {
        String zoneId = getResources().getString(R.string.adprompt_zone_id);
        SnakkAdPrompt adPrompt = SnakkAdvertising.get().getAdPromptForZone(this, zoneId);
        adPrompt.show();
    }

    public void advancedAdPromptExample() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.adprompt_zone_id);
        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
                                                   .setTestMode(true)
                                    .getPwAdRequest();

        // get an ad instance using request
        SnakkAdPrompt adPrompt = SnakkAdvertising.get().getAdPrompt(this, request);

        // register for ad lifecycle callbacks
        adPrompt.setListener(new SnakkAdPrompt.SnakkAdPromptListener() {
            @Override
            public void adPromptDidLoad(SnakkAdPrompt ad) {
                // show ad as soon as it's loaded
                Log.d(TAG, "AdPrompt Loaded");
                ad.show();
            }

            @Override
            public void adPromptDisplayed(SnakkAdPrompt ad) {
                Log.d(TAG, "Ad Prompt Displayed");
            }

            @Override
            public void adPromptDidFail(SnakkAdPrompt ad, String error) {
                Log.d(TAG, "Ad Prompt Error: " + error);
            }

            @Override
            public void adPromptClosed(SnakkAdPrompt ad, boolean didAccept) {
                String btnName = didAccept ? "YES" : "NO";
                Log.d(TAG, "Ad Prompt Closed with \"" + btnName + "\" button");
            }
        });

        // load ad... we'll be notified when it's ready
        adPrompt.load();
    }


    public void simpleInterstitialExample() {
        String zoneId = getResources().getString(R.string.intrs_zone_id);
        SnakkInterstitialAd interstitialAd = SnakkAdvertising.get().getInterstitialAdForZone(this, zoneId);
        interstitialAd.show();
    }


    public void advancedInterstitialExample() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.intrs_zone_id);

        Map<String,String> params = new HashMap<String, String>(1);
//        params.put("cid", "238409");
        params.put("cid", "177735");
//        params.put("cid", "69558"); // fullscreen ad
//        params.put("cid", "66218"); // medium rect ad

        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
//                                                                    .setTestMode(true)
                                                                    .setCustomParameters(params)
                                                        .getPwAdRequest();

        // get an ad instance using request
        SnakkInterstitialAd interstitialAd = SnakkAdvertising.get().getInterstitialAd(this, request);

        // register for ad lifecycle callbacks
        interstitialAd.setListener(new SnakkInterstitialAd.SnakkInterstitialAdListener() {
            @Override
            public void interstitialDidLoad(SnakkInterstitialAd ad) {
                // show ad as soon as it's loaded
                Log.d(TAG, "Interstitial Did Load");
                ad.show();
            }

            @Override
            public void interstitialDidClose(SnakkInterstitialAd ad) {
                Log.d(TAG, "Interstitial Did Close");
            }

            @Override
            public void interstitialDidFail(SnakkInterstitialAd ad, String error) {
                Log.d(TAG, "Interstitial Did Fail: " + error);
            }

            @Override
            public void interstitialActionWillLeaveApplication(SnakkInterstitialAd ad) {
                Log.d(TAG, "Interstitial Will Leave App");
            }
        });

        // load ad... we'll be notified when it's ready
        interstitialAd.load();
    }


    public void simpleVideoExample() {
        String zoneId = getResources().getString(R.string.video_zone_id);
        SnakkVideoInterstitialAd videoAd = SnakkAdvertising.get().getVideoInterstitialAdForZone(this, zoneId);
        videoAd.show();
    }


    public void advancedVideoExample() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.video_zone_id);
        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
                                                                    .setTestMode(true)
                                                        .getPwAdRequest();

        // get an ad instance using request
        SnakkVideoInterstitialAd videoAd = SnakkAdvertising.get().getVideoInterstitialAd(this, request);

        // register for ad lifecycle callbacks
        videoAd.setListener(new SnakkVideoInterstitialAd.SnakkVideoInterstitialAdListener() {
            @Override
            public void videoInterstitialDidLoad(SnakkVideoInterstitialAd ad) {
                // show ad as soon as it's loaded
                Log.d(TAG, "VideoAd Did Load");
                ad.show();
            }

            @Override
            public void videoInterstitialDidClose(SnakkVideoInterstitialAd ad) {
                Log.d(TAG, "videoInterstitialDidClose");
            }

            @Override
            public void videoInterstitialDidFail(SnakkVideoInterstitialAd ad, String error) {
                Log.d(TAG, "videoInterstitialDidFail: " + error);
            }

            @Override
            public void videoInterstitialActionWillLeaveApplication(SnakkVideoInterstitialAd ad) {
                Log.d(TAG, "videoInterstitialActionWillLeaveApplication");
            }
        });

        // load ad... we'll be notified when it's ready
        videoAd.load();
    }


    public void simpleBannerExample() {
        SnakkBannerAdView bannerAdView = (SnakkBannerAdView)findViewById(R.id.bannerAdView);
        String zoneId = getResources().getString(R.string.banner_zone_id);
        bannerAdView.startRequestingAdsForZone(zoneId);
    }

    public void advancedBannerExample() {
        Log.d(TAG, "advancedBannerExample");
        // find the view in your layout
        SnakkBannerAdView bannerAdView = (SnakkBannerAdView)findViewById(R.id.bannerAdView);

//        bannerAdView.setAdUpdateInterval(0); // no auto rotation
        bannerAdView.setAdUpdateInterval(30);

        // generate a customized request
        String zoneId = getResources().getString(R.string.banner_zone_id);

        Map<String, String> params = new HashMap<String, String>(1);
//        params.put("cid", "238409");
        params.put("cid", "141297"); // samsung banner
//        params.put("cid", "238447");

        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
//                                                                    .setTestMode(true)
//                                                                    .setCustomParameters(params)
                                                            .getPwAdRequest();

        // register for ad lifecycle callbacks
        bannerAdView.setListener(new SnakkBannerAdView.BannerAdListener() {
            @Override
            public void onReceiveBannerAd(SnakkBannerAdView ad) {
                Log.d(TAG, "Banner onReceiveBannerAd");
            }

            @Override
            public void onBannerAdError(SnakkBannerAdView ad, String errorMsg) {
                Log.d(TAG, "Banner onBannerAdError: " + errorMsg);
            }

            @Override
            public void onBannerAdFullscreen(SnakkBannerAdView ad) {
                Log.d(TAG, "Banner onBannerAdFullscreen");
            }

            @Override
            public void onBannerAdDismissFullscreen(SnakkBannerAdView ad) {
                Log.d(TAG, "Banner onBannerAdDismissFullscreen");
            }

            @Override
            public void onBannerAdLeaveApplication(SnakkBannerAdView ad) {
                Log.d(TAG, "Banner onBannerAdLeaveApplication");
            }
        });

        // start banner rotating
        bannerAdView.startRequestingAds(request);
    }


    public void fireAdPrompt(View sender) {
        simpleAdPromptExmple();
//        advancedAdPromptExample();
    }

    public void fireInterstitial(View sender) {
//        simpleInterstitialExample();
        advancedInterstitialExample();
    }

    public void fireVideoInterstitial(View sender) {
        simpleVideoExample();
//        advancedVideoExample();
    }

    public void fireBanner(View sender) {
//        simpleBannerExample();
        advancedBannerExample();
    }
}
