package com.snakk.adview;

import android.content.Context;
import android.view.View;

public class DeprecatedInterstitialBaseView extends AdInterstitialBaseView {
    public DeprecatedInterstitialBaseView(Context ctx, String zone) {
        super(ctx, zone);
    }

    public void load() {
        if(interstitialListener != null) {
            interstitialListener.error(this, this.getClass() + " is deprecated.");
        }
    }


    @Override
    public void end(AdViewCore adView) {
        // noop
    }

    @Override
    public void didResize(AdViewCore adView) {
        // noop
    }

    @Override
    public void showInterstitial() {
        if(interstitialListener != null) {
            interstitialListener.error(this, this.getClass() + " is deprecated.");
        }
    }

    @Override
    public void playVideo(final String url, final String clickUrl, boolean audioMuted, boolean autoPlay,
                          boolean controls, boolean loop, Dimensions d, String startStyle, String stopStyle) {
        // noop
    }

    public void interstitialShowing() {
        // noop
    }

    @Override
    public void interstitialClosing() {
        // noop
    }


    @Override
    protected void interstitialClose() {
        // noop
    }

    @Override
    public View getInterstitialView(Context ctx) {
        // noop
        return new View(ctx);
    }}
