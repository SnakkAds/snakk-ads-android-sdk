package com.snakkads.tabsswipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snakk.advertising.*;

import android.os.Build;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.snakkads.tabsswipe.R;

import java.util.HashMap;
import java.util.Map;

public class BannerStandardMediaFragment extends Fragment {
    public static String url;
    private final static String TAG = "Snakk";
    Context context;
    private WebView mWebView;
    View rootView;
    ProgressDialog progressBar;
    SnakkBannerAdView bannerAdView;
    public BannerStandardMediaFragment() {

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_standard, container, false);
        progressBar = ProgressDialog.show(getActivity(), "",
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
                    bannerAdView = (SnakkBannerAdView)rootView.findViewById(R.id.bannerAd);
                    loadBanner();
                }
            }
        });


        //web.getSettings().setLoadWithOverviewMode(true);
        //web.getSettings().setUseWideViewPort(true);
        //web.getSettings().setBuiltInZoomControls(true);
        url = getResources().getString(R.string.url);
        Log.e(TAG,  getResources().getString(R.string.banner_standard_zone_id));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
		return rootView;
	}
    public void loadBanner() {
        Log.d(TAG, "loadBanner");
        // find the view in your layout

//        bannerAdView.setAdUpdateInterval(0); // no auto rotation
        bannerAdView.setAdUpdateInterval(30);

        // generate a customized request
        String zoneId = getResources().getString(R.string.banner_standard_zone_id);

        Map<String, String> params = new HashMap<String, String>(1);
        String cid = getResources().getString(R.string.banner_standard_zone_cid);
        params.put("cid", cid);

        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
//                                                                    .setTestMode(true)
                .setCustomParameters(params)
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
    }}
