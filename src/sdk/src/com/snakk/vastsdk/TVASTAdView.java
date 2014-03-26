package com.snakk.vastsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.snakk.advertising.internal.SnakkAdActivity;

public class TVASTAdView extends WebView {

    private AdViewListener m_listener;
    private boolean isFullscreen = false;
    private boolean isHidden = false;

    private enum State {NEW, LOADING, SHOWN, CLICKED, DONE}

    private State m_state;

    // initial size of the ad creative, not including any borders
    // note, ad may expand to a larger size once user begins to interact
    public TVASTAdView(Context context) {
        super(context);
        init();
    }

    public TVASTAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TVASTAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setWebViewClient(new AdWebViewClient(getContext()));
        WebChromeClient mWebChromeClient = new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return true;
            }
        };
        setWebChromeClient(mWebChromeClient);
        m_state = State.NEW;
    }

    @Override
    public void loadUrl(String url) {

        boolean isGooglePlay = isGooglePlayUrl(url);

        if (isGooglePlay) {
            openUrlInExternalBrowser(getContext(), url);
            m_state = State.DONE;
        }
        else {
            super.loadUrl(url);
        }
    }

    protected void loadHtml(String html) {
        String baseUrl = TVASTAdsRequest.SERVER_BASE_URL;
        String mimeType = "text/html";
        String encoding = "UTF-8";
        String historyUrl = "about:blank";
        super.loadDataWithBaseURL(baseUrl, html, mimeType, encoding, historyUrl);
    }

    protected void hideWhileLoading() {
        //TODO give user some indication that page is loading...
        if (isHidden) {
            return;
        }
        setVisibility(INVISIBLE);
        isHidden = true;
    }


    protected void expandToFullscreen() {
        if(isFullscreen) {
            return;
        }
        ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(lp);
        requestLayout();
        setBackgroundColor(Color.BLACK);
        setVisibility(VISIBLE);
        isFullscreen = true;
    }

    private class AdWebViewClient extends WebViewClient {
        private Context context;

        public AdWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (m_state == State.SHOWN) {
                if (m_listener != null) {
                    m_listener.onClicked(TVASTAdView.this);
                }
                m_state = State.CLICKED;
            }

            boolean isGooglePlay = isGooglePlayUrl(url);

            if (isGooglePlay) {
                openUrlInExternalBrowser(context, url);
                m_state = State.DONE;
                return true;
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (m_state == State.NEW) {
                m_state = State.LOADING;
            }
            else if (m_state == State.CLICKED) {
                hideWhileLoading();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (m_state == State.LOADING) {
                if (m_listener != null) {
                    m_listener.onLoaded(TVASTAdView.this);
                }
                m_state = State.SHOWN;
            } else if (m_state == State.CLICKED) {
                expandToFullscreen();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            if (m_listener != null) {
                String error = description + "(" + failingUrl + ")";
                m_listener.onError(TVASTAdView.this, error);
            }
            m_state = State.DONE;
        }
    }

    private boolean isGooglePlayUrl(String url) {
        return url.startsWith("market://details?") ||
                url.startsWith("http://market.android.com/details?") ||
                url.startsWith("https://market.android.com/details?") ||
                url.startsWith("http://play.google.com/store/apps/details?") ||
                url.startsWith("https://play.google.com/store/apps/details?");
    }

    private void openUrlInExternalBrowser(Context context, String url) {
        if (m_listener != null) {
            m_listener.willLeaveApplication(this);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (context instanceof SnakkAdActivity) {
            ((Activity)context).startActivityForResult(intent, 3);
        }
        else {
            context.startActivity (intent);
        }
    }

    public void setAdViewListener(AdViewListener listener) {
        m_listener = listener;
    }

    public interface AdViewListener {
        public void onLoaded(TVASTAdView adView);

        public void onError(TVASTAdView adView, String error);

        public void onClicked(TVASTAdView adview);

        public void willLeaveApplication(TVASTAdView adview);
    }
}
