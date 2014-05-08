package com.yourcompany.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.snakk.advertising.*;


public class AdvertisingSample extends Activity {

    private final static String TAG = "AdvertisingSample";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // test that you've integrated properly
        // NOTE: remove this before your app goes live!
        SnakkAdvertising.get().validateSetup(this);
    }


    public void simpleInterstitialExample() {
        String zoneId = getResources().getString(R.string.intrs_zone_id);
        SnakkInterstitialAd interstitialAd = SnakkAdvertising.get().getInterstitialAdForZone(this, zoneId);
        interstitialAd.show();
    }


    public void advancedInterstitialExample() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.intrs_zone_id);

        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
                                                                // enable during the development phase
                                                                .setTestMode(true)
                                                        .getSnakkAdRequest();

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
                                                                // enable during the development phase
                                                                .setTestMode(true)
                                                        .getSnakkAdRequest();

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

        // Banner rotation interval; defaults to 60 seconds.
//        bannerAdView.setAdUpdateInterval(0); // no auto rotation
        bannerAdView.setAdUpdateInterval(30); // rotate every 30 seconds.

        // generate a customized request
        String zoneId = getResources().getString(R.string.banner_zone_id);

        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
                                                                    // enable during the development phase
                                                                    .setTestMode(true)

//                                                                    // enable automatic gps based location tracking
//                                                                    .setLocationTrackingEnabled(true)

//                                                                    // optional keywords for custom targeting
//                                                                    .setKeywords(Arrays.asList("keyword1", "keyword2"))
                                                            .getSnakkAdRequest();

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

//        // Optionally set location manually.
//        double lat = 40.7787895;
//        double lng = -73.9660945;
//        bannerAdView.updateLocation(lat, lng);

        // start banner rotating
        bannerAdView.startRequestingAds(request);
    }


    public void fireInterstitial(View sender) {
//        simpleInterstitialExample();
        advancedInterstitialExample();
    }

    public void fireVideoInterstitial(View sender) {
//        simpleVideoExample();
        advancedVideoExample();
    }

    public void fireBanner(View sender) {
//        simpleBannerExample();
        advancedBannerExample();
    }
}
