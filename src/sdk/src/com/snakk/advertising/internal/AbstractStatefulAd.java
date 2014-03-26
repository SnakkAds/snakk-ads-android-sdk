package com.snakk.advertising.internal;

import com.snakk.core.SnakkLog;

/**
 * logic for tracking ad state, such as ignoring load requests to ads that are
 * currently loading, and handling automatic "load and show" functionality when
 * end user calls ad.show() before ad.load().
 *
 * This class is not thread safe.
 */
public abstract class AbstractStatefulAd {
    protected static final String TAG = "Snakk";

    protected static enum State { NEW, LOADING, LOADED, SHOWN, DONE }
    protected boolean showImmediately = false;
    protected State state = State.NEW;

    /**
     * moves to the next state, if particular state transition is allowed.  If
     * transition is not allowed, state is not changed.
     * @param newState the state to update to
     * @return true if state change was successful, false if it was blocked.
     */
    protected boolean ratchetState(State newState) {
        if (newState.compareTo(state) > 0) {
            state = newState;
            return true;
        }
        SnakkLog.d(TAG, "Invalid state transition: " + state + " -> " + newState);
        return false;
    }

    /**
     * fill this in w/ the actual ad loading behavior, such as making a request
     * to the ad server
     */
    public abstract void doLoad();

    public void load() {
        if (ratchetState(State.LOADING)) {
            doLoad();
        }
        else if (state == State.LOADING) {
            // currently loading... do nothing
            SnakkLog.d(TAG, "Ignoring attempt to load interstitial... already loading!");
        }
        else {
            // already been loaded... don't reuse interstitials!
            SnakkLog.w(TAG, "Ignoring attempt to re-load interstitial.");
        }
    }

    public boolean isLoaded() {
        return (state == State.LOADED);
    }

    /**
     * fill this in w/ the actual ad show behavior, such as displaying ad view,
     * or popping up an activity.
     */
    public abstract void doShow();

    public void show() {
        switch (state) {
            case NEW:
                SnakkLog.v(TAG, "Loading ad asynchronously before showing");
                load();
                // fall through
            case LOADING:
                // mark to display as soon as interstitial is rdy
                showImmediately = true;
                break;

            case LOADED:
                doShow();
                ratchetState(State.SHOWN);

                break;

            case SHOWN:
            case DONE:
            default:
                SnakkLog.w(TAG, "Ignoring attempt to re-use interstitial.");
                break;
        }

    }
}
