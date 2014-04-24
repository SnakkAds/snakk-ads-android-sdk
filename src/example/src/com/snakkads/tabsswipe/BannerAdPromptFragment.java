package com.snakkads.tabsswipe;

import com.snakkads.tabsswipe.R;
import android.app.ProgressDialog;
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

import com.snakk.advertising.*;

import android.content.Context;
import android.webkit.WebViewClient;

public class BannerAdPromptFragment extends Fragment {
    public static String url;
    private final static String TAG = "Snakk";
    Context context;
    private WebView mWebView;
    View rootView;
    public BannerAdPromptFragment(){

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadAdPrompt();
        }
        else {

        }
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
        rootView = inflater.inflate(R.layout.fragment_adprompt, container, false);
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
        Log.e(TAG,  getResources().getString(R.string.adprompt_zone_id));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        return rootView;
    }
     public void loadAdPrompt() {
        // generate a customized request
        String zoneId = getResources().getString(R.string.adprompt_zone_id);
        SnakkAdRequest request = SnakkAdvertising.get().getAdRequestBuilder(zoneId)
        //        .setTestMode(true)
                .getPwAdRequest();

        // get an ad instance using request
        SnakkAdPrompt adPrompt = SnakkAdvertising.get().getAdPrompt(getActivity(), request);

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
}