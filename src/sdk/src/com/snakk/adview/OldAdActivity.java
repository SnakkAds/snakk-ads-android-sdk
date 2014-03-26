package com.snakk.adview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.snakk.adview.AdViewCore.OnInterstitialAdDownload;

@Deprecated
public class OldAdActivity extends Activity {
    public static AdInterstitialBaseView adView; // this will only be set for interstitials
    public static AdViewCore callingAdView; // this will only be set for banner ads

    protected WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String clickURL = getIntent().getStringExtra("com.snakk.adview.ClickURL");
        if(clickURL != null) {
            setupWebView(savedInstanceState);
        }
        else {
            if(adView != null) {
                setContentView(adView.getInterstitialView(this));
                adView.interstitialShowing();
            }
            else {
                finish();

            }
        }
    }

    private void setupWebView(Bundle savedInstanceState) {
        WebView webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, 1f));
        webView.loadUrl(getIntent().getDataString());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setLoadWithOverviewMode(true); // not supported by older versions of Android
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setUseWideViewPort(true);

        setContentView(webView);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")){
                    try {
                        if(adView != null) {
                            OnInterstitialAdDownload listener = adView.getOnInterstitialAdDownload();
                            if(listener != null) {
                                listener.willLeaveApplication(adView);
                            }
                        }
                        else if(callingAdView != null) {
                            AdViewCore.OnAdDownload listener = callingAdView.getOnAdDownload();
                            if(listener != null) {
                                listener.willLeaveApplication(callingAdView);
                            }
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent,3);
                    }
                    catch(Exception e) {
                        Log.e("Snakk", "An error occured", e);
                    }
                    return true;
                }
                view.loadUrl(url);
                setTitle(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                setProgress(newProgress * 100); // Make the bar disappear after
                                                // URL is loaded
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (adView != null) {
                adView.onKeyDown(keyCode, event);
            }
            finish();
            return true;
        }
        return false;
    }


    @Override
    public void finish() {
        if(adView != null) {
            adView.interstitialClosing();
            adView.removeViews();
            adView = null;
        }
        if(callingAdView != null) {
            callingAdView.willDismissFullScreen();
            callingAdView = null;
        }
        super.finish();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        // close activity when app returns to foreground
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(adView != null) {
//            adView.closeInterstitial();
//        }
    }

    @Override
    protected void onDestroy() {
//        mLayout.removeAllViews();
        super.onDestroy();
    }
}
