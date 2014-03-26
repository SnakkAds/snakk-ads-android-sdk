package com.snakk.advertising;

/**
 * AdPrompts are a simple ad unit designed to have a native feel.
 * The user is given the option to download an app, and if they accept,
 * they are taken to the app within the app marketplace.
 *
 * <h3>Simple example:</h3>
 * <pre>
 * SnakkAdPrompt adPrompt = SnakkAdvertising.getAdPromptForZone(context, "YOUR_ZONE_ID");
 * adPrompt.show(); // AdPrompt is loaded and shown asynchronously
 * </pre>
 *
 * Instances of SnakkAdPrompt are not safe for use by multiple threads.
 */
public interface SnakkAdPrompt {

    /**
     * Implement this interface to be notified of AdPrompt lifecycle changes.
     */
    public interface SnakkAdPromptListener {
        /**
         * Fired after the AdPrompt is loaded and ready to be displayed.
         * @param adPrompt the adPrompt that was loaded successfully
         */
        public void adPromptDidLoad(SnakkAdPrompt adPrompt);

        /**
         * Fired after the AdPrompt is displayed.
         * @param adPrompt the adPrompt that was displayed successfully
         */
        public void adPromptDisplayed(SnakkAdPrompt adPrompt);

        /**
         * Fired if the AdPrompt failed to load.
         * @param adPrompt the adPrompt that failed to load
         * @param error description of error.  Error messages are for debugging
         * purposes, and generally should not be shown to the end user.
         */
        public void adPromptDidFail(SnakkAdPrompt adPrompt, String error);

        /**
         * Fired if the AdPrompt was closed.
         * @param adPrompt the adPrompt that was closed
         * @param didAccept true if user pressed the call to action button, false otherwise
         */
        public void adPromptClosed(SnakkAdPrompt adPrompt, boolean didAccept);

    }

    /**
     * @return the listener instance that will be notified of ad lifecycle events
     */
    SnakkAdPromptListener getListener();

    /**
     * @param adPromptListener the listener instance that will be notified
     * of ad lifecycle events
     */
    void setListener(SnakkAdPromptListener adPromptListener);

    /**
     * @return true if ad is ready for display onscreen, false otherwise
     */
    boolean isLoaded();

    /**
     * Fire off an asynchronous request to server for an AdPrompt.
     * Use {@link SnakkAdPromptListener} or {@link #isLoaded()} to determine when
     * AdPrompt is loaded.
     */
    void load();

    /**
     * Display AdPrompt to user.  If AdPrompt is not loaded, {@link #load()}
     * will be called and the AdPrompt will be loaded and shown asynchronously.
     * Use {@link SnakkAdPromptListener} or {@link #isLoaded()}  to determine when
     * AdPrompt is loaded.
     */
    void show();
}
