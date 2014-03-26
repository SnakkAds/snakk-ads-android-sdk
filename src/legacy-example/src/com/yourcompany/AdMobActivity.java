package com.yourcompany;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.Log;
import android.widget.LinearLayout;
import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;


public class AdMobActivity extends Activity {

    // these are sample placement id's.  Get yours from the Admob dashboard.
    private static final String BANNER_UNIT_ID = "903dbd7178b049f7";
    private static final String INTERSTITIAL_UNIT_ID = "3027767e23364ccf";

    private InterstitialAd googInterstitial = null;

    private Button gLoadButton = null;
    private Button gShowButton = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admoblayout);

        setupGoogle();
        setupGoogInterstitial();
    }


    private void setupGoogle() {
        // Setup the google related buttons
        gLoadButton = (Button) findViewById(R.id.gLoadInterstitialButton);
        gLoadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                setupGoogInterstitial();
            }
        });
        gShowButton = (Button) findViewById(R.id.gShowInterstitialButton);
        gShowButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                googInterstitial.show();
            }
        });
        gShowButton.setEnabled(false);

        com.google.ads.AdView googAd = new com.google.ads.AdView(this, AdSize.BANNER, BANNER_UNIT_ID);
        googAd.setAdListener(new AdListener() {

            @Override
            public void onDismissScreen(Ad gAd) {
                Log.d("SnakkTest", "googAd->onDismissScreen");
            }

            @Override
            public void onFailedToReceiveAd(Ad gAd, ErrorCode errCode) {
                Log.d("SnakkTest", "googAd->onFailedToReceiveAd: " + errCode);
            }

            @Override
            public void onLeaveApplication(Ad gAd) {
                Log.d("SnakkTest", "googAd->onLeaveApplication");
            }

            @Override
            public void onPresentScreen(Ad gAd) {
                Log.d("SnakkTest", "googAd->onPresentScreen");
            }

            @Override
            public void onReceiveAd(Ad gAd) {
                Log.d("SnakkTest", "googAd->onReceiveAd");
            }

        });

        com.google.ads.AdRequest googAdRequest = new com.google.ads.AdRequest();
        googAdRequest.addTestDevice(com.google.ads.AdRequest.TEST_EMULATOR);
        googAdRequest.addTestDevice("ED9A71101B1CD7741894D3D1B181D51B");

        LinearLayout layout = (LinearLayout) findViewById(R.id.googLayout);

        // Add the adView to it
        layout.addView(googAd);

        // Initiate a generic request to load it with an ad
        googAd.loadAd(googAdRequest);
    }


    private void setupGoogInterstitial() {
        com.google.ads.AdRequest googAdRequest = new com.google.ads.AdRequest();
        googAdRequest.addTestDevice(com.google.ads.AdRequest.TEST_EMULATOR);
        googAdRequest.addTestDevice("ED9A71101B1CD7741894D3D1B181D51B");

        googInterstitial = new com.google.ads.InterstitialAd(this, INTERSTITIAL_UNIT_ID);
        googInterstitial.loadAd(googAdRequest);
        googInterstitial.setAdListener(new AdListener() {

            @Override
            public void onDismissScreen(Ad arg0) {
                Log.d("SnakkTest", "googInterstitial->onDismissScreen");
                gShowButton.setEnabled(false);
                gLoadButton.setEnabled(true);
            }

            @Override
            public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
                Log.d("SnakkTest", "googInterstitial->onFailedToReceiveAd: " + arg1);
                gShowButton.setEnabled(false);
                gLoadButton.setEnabled(true);
            }

            @Override
            public void onLeaveApplication(Ad arg0) {
                Log.d("SnakkTest", "googInterstitial->onLeaveApplication");
            }

            @Override
            public void onPresentScreen(Ad arg0) {
                Log.d("SnakkTest", "googInterstitial->onPresentScreen");
            }

            @Override
            public void onReceiveAd(Ad arg0) {
                Log.d("SnakkTest", "googInterstitial->onReceiveAd");
                gShowButton.setEnabled(true);
            }
        });
    }

}