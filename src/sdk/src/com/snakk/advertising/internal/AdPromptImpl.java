package com.snakk.advertising.internal;

import android.content.Context;
import com.snakk.advertising.SnakkAdPrompt;
import com.snakk.advertising.SnakkAdRequest;
import com.snakk.adview.AdPrompt;

public final class AdPromptImpl implements SnakkAdPrompt {

    private final AdPrompt legacyAdPrompt;
    private SnakkAdPromptListener listener = null;


    public static SnakkAdPrompt getAdPromptForZone(Context context, String zone) {
        SnakkAdRequest request = new AdRequestImpl.BuilderImpl(zone).getPwAdRequest();
        return getAdPrompt(context, request);
    }

    /**
     * Factory method used to build AdPrompt instances
     * @param context the activity instance
     * @param request the request object containing configuration details
     * @return An AdPrompt object that is ready to be loaded.
     * Call {@link #load()} to initiate ad request.
     */
    public static SnakkAdPrompt getAdPrompt(Context context, SnakkAdRequest request) {
        return new AdPromptImpl(context, request);
    }


    private AdPromptImpl(Context context, SnakkAdRequest adRequest) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }

        if (adRequest == null) {
            throw new NullPointerException("Ad request cannot be null");
        }

        legacyAdPrompt = new AdPrompt(context, AdRequestImpl.asImplAdRequest(adRequest));
    }


    @Override
    public SnakkAdPromptListener getListener() {
        return listener;
    }

    @Override
    public void setListener(final SnakkAdPromptListener adPromptListener) {
        if (adPromptListener != null) {
            legacyAdPrompt.setListener(new AdPrompt.AdPromptCallbackListener() {
                @Override
                public void adPromptLoaded(AdPrompt adPrompt) {
                    adPromptListener.adPromptDidLoad(AdPromptImpl.this);
                }

                @Override
                public void adPromptDisplayed(AdPrompt adPrompt) {
                    adPromptListener.adPromptDisplayed(AdPromptImpl.this);
                }

                @Override
                public void adPromptError(AdPrompt adPrompt, String error) {
                    adPromptListener.adPromptDidFail(AdPromptImpl.this, error);
                }

                @Override
                public void adPromptClosed(AdPrompt adPrompt, boolean didAccept) {
                    adPromptListener.adPromptClosed(AdPromptImpl.this, didAccept);
                }
            });
        }
        else {
            legacyAdPrompt.setListener(null);
        }
        this.listener = adPromptListener;
    }

    @Override
    public final boolean isLoaded() {
        return legacyAdPrompt.isLoaded();
    }

    @Override
    public final void load() {
        legacyAdPrompt.load();
    }

    @Override
    public final void show() {
        legacyAdPrompt.showAdPrompt();
    }
}
