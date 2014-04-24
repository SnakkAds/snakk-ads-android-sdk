package com.snakkads.tabsswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.snakk.advertising.*;
import com.snakk.advertising.internal.DeviceCapabilities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.snakkads.tabsswipe.R;

import java.util.HashMap;
import java.util.Map;

import android.content.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.ProgressDialog;
import android.app.Activity;
public class BannerInterstitialFragment extends Fragment {
    public static String url;
    private final static String TAG = "Snakk";
    Context context;
    SnakkInterstitialAd interstitialAd;
    private WebView mWebView;
    View rootView;
    ProgressDialog progressBar;
    Activity act;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadInterstitial();
        }
        else {

        }
    }

    public BannerInterstitialFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

		rootView = inflater.inflate(R.layout.fragment_interstitial, container, false);
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
                }
            }
        });


        //web.getSettings().setLoadWithOverviewMode(true);
        //web.getSettings().setUseWideViewPort(true);
        //web.getSettings().setBuiltInZoomControls(true);
        url = getResources().getString(R.string.url);
        Log.e(TAG,  getResources().getString(R.string.intrs_zone_id));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        return rootView;
	}
      public void loadInterstitial() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.intrs_zone_id);

        Map<String, String> params = new HashMap<String, String>(1);
        String cid = getResources().getString(R.string.intrs_zone_cid);
        params.put("cid", cid);

        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
//                                                                    .setTestMode(true)
                .setCustomParameters(params)
                .getPwAdRequest();

        // get an ad instance using request
        interstitialAd = SnakkAdvertising.get().getInterstitialAd(getActivity(), request);

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
}
