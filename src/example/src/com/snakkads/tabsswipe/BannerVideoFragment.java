package com.snakkads.tabsswipe;

import com.snakkads.tabsswipe.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.util.Log;
import android.webkit.WebViewClient;

import com.snakk.advertising.*;

public class BannerVideoFragment extends Fragment {
    public static String url;
    private final static String TAG = "Snakk";
    Context context;
    SnakkInterstitialAd interstitialAd;
    private WebView mWebView;
    View rootView;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadVideo();
        }
        else {

        }
    }
    public BannerVideoFragment() {

    }
    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    /**
     * Called when the view has been detached from the fragment. Destroys the WebView.
     */
    @Override
    public void onDestroyView() {
        mWebView.destroy();
        mWebView = null;
        super.onDestroyView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_video, container, false);
        final ProgressDialog progressBar = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);

        mWebView=(WebView)rootView.findViewById(R.id.webView_content);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();

                }
            }
        });


        //web.getSettings().setLoadWithOverviewMode(true);
        //web.getSettings().setUseWideViewPort(true);
        //web.getSettings().setBuiltInZoomControls(true);
        url = getResources().getString(R.string.url);
        Log.e(TAG,  getResources().getString(R.string.video_zone_id));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        return rootView;
    }

    public void loadVideo() {
        String zoneId = getResources().getString(R.string.video_zone_id);
        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
    //            .setTestMode(true)
                .getPwAdRequest();

        // get an ad instance using request
        SnakkVideoInterstitialAd videoAd = SnakkAdvertising.get().getVideoInterstitialAd(getActivity(), request);

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
}