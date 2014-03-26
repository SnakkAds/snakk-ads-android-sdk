package com.snakk.adview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import android.view.View;
import com.snakk.advertising.internal.*;
import com.snakk.advertising.internal.SnakkAdActivity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

public class AdPrompt {
    private static final String AD_TYPE_DIALOG = "10";

    protected Context context;
    protected AdRequest adRequest;

    private AdPromptCallbackListener listener;
    private LoadContentTask contentTask;

    private boolean showAfterLoad; // set by .showAdPrompt() when called on an AdPrompt that hasn't been loaded
    private boolean loaded;
    private String title;
    private String html;
    private String callToAction;
    private String declineStr;
    private String clickUrl;


    public AdPrompt(Context context, String zone) {
        this(context, new AdRequest(zone));
    }

    public AdPrompt(Context context, AdRequest request) {
        this.context = context;
        adRequest = request;
        adRequest.setAdtype(AD_TYPE_DIALOG);
        adRequest.initDefaultParameters(context);
        loaded = false;
    }

    /**
     * Optional. Set user location longtitude value (given in degrees.decimal
     * degrees).
     *
     * @param longitude
     */
    public void setLongitude(String longitude) {
        if ((adRequest != null) && (longitude != null)) {
            adRequest.setLongitude(longitude);
        }
    }

    /**
     * Optional. Get user location longtitude value (given in degrees.decimal
     * degrees).
     */
    public String getLongitude() {
        if (adRequest != null) {
            String longitude = adRequest.getLongitude();

            if (longitude != null) {
                return longitude;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @deprecated Use setCustomParameters(Map<String, String> customParameters) instead
     * Optional. Set Custom Parameters.
     *
     * @param customParameters
     */
    @Deprecated
    public void setCustomParameters(Hashtable<String, String> customParameters) {
        if (adRequest != null) {
            adRequest.setCustomParameters(customParameters);
        }
    }

    /**
     * Optional. Set Custom Parameters.
     *
     * @param customParameters a map containing parameters to add
     */
    public void setCustomParameters(Map<String, String> customParameters) {
        if (adRequest != null) {
            adRequest.setCustomParameters(customParameters);
        }
    }

    /**
     * Set listener for AdPrompt callbacks
     *
     * @param listener
     */
    public void setListener(AdPromptCallbackListener listener) {
        this.listener = listener;
    }

    public void load() {
        contentTask = new LoadContentTask(this);
        contentTask.execute(0);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void showAdPrompt() {
        if(!loaded) {
            showAfterLoad = true; // show immediately after loading...
            load();
        }
        else {
            displayAdPrompt(title, html, callToAction, declineStr, clickUrl);
        }
    }

    private void displayAdPrompt(String title, String html, String callToAction, String declineStr, final String clickUrl) {
        final Activity theActivity = (Activity)context;
        final AdPromptCallbackListener theListener = listener;
        final AdPrompt theAdPrompt = this;

        try {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setPositiveButton(callToAction, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        // Spawn off thread to avoid ANR's on really slow devices...
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                SnakkAdActivity.startActivity(context, buildWrapper());
                                if (theListener != null){
                                    theListener.adPromptClosed(theAdPrompt, true);
                                }
                            }
                        };
                        new Thread( r ).start();
                    }
                })
                .setNegativeButton(declineStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        // cancel out...
                        if (theListener != null){
                            theListener.adPromptClosed(theAdPrompt, false);
                        }
                    }
                }).create();
            alertDialog.setTitle(title);

            alertDialog.show();
            if(listener != null) {
                listener.adPromptDisplayed(this);
            }
        } catch(Exception e) {
            if(listener != null) {
                listener.adPromptError(this, e.getMessage());
            }
            Log.e("Snakk", "An error occurred while attempting to display AdPrompt", e);
        }
    }

    private AdActivityContentWrapper buildWrapper() {
        AdActivityContentWrapper wrapper = new AdActivityContentWrapper() {
            private BasicWebView webView = null;

            @Override
            public View getContentView(SnakkAdActivity activity) {
                if (webView == null) {
                    webView = new BasicWebView(activity);
                    webView.loadUrl(clickUrl);
                }

                return webView;
            }

            @Override
            public void done() {
            }
        };
        return wrapper;
    }

    private String requestGet(String url) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        Log.d("Snakk", url);
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 8192);
        String responseValue = readInputStream(bufferedInputStream);
        Log.d("Snakk", responseValue);
        bufferedInputStream.close();
        inputStream.close();
        return responseValue;
    }

    private static String readInputStream(BufferedInputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] buffer = new byte[8192];
        for (int n; (n = in.read(buffer)) != -1;) {
            out.append(new String(buffer, 0, n));
        }
        return out.toString();
    }

    private class LoadContentTask extends AsyncTask<Integer, Integer, String>{

        private AdPrompt theAdPrompt;

        public LoadContentTask(AdPrompt adPrompt) {
            theAdPrompt = adPrompt;
        }

        @Override
        protected String doInBackground(Integer... params) {

            adRequest.initDefaultParameters(context);
            String url = adRequest.createURL();
            String data;
            try {
                data = requestGet(url);
            } catch (IOException e) {
                data = "{\"error\": \"" + e.getMessage() + "\"}";
            }
            return data;
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            String error = null;
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                if(jsonObject.has("error")) {
                    // failed to retrieve an ad, abort and call the error callback
                    if(listener != null) {
                        listener.adPromptError(theAdPrompt, jsonObject.getString("error"));
                    }
                }
                else if (jsonObject.has("type") && "alert".equals(jsonObject.getString("type"))) {
                    title = jsonObject.getString("adtitle");
                    if (jsonObject.has("html")) {
                        html = jsonObject.getString("html");
                    }
                    callToAction = jsonObject.getString("calltoaction");
                    declineStr = jsonObject.getString("declinestring");
                    clickUrl = jsonObject.getString("clickurl");
                    loaded = true;
                    if(listener != null) {
                        listener.adPromptLoaded(theAdPrompt);
                    }

                    if(showAfterLoad) {
                        showAdPrompt();
                    }
                }
                else {
                    if(listener != null) {
                        listener.adPromptError(theAdPrompt, "Server returned an incompatible ad");
                    }
                }

            } catch (JSONException e) {
                if("".equals(jsonStr)) {
                    error = "server returned an empty response";
                }
                else {
                    error = e.getMessage();
                }
                if(listener != null) {
                    listener.adPromptError(theAdPrompt, error);
                }
            }
        }
    }

    /**
     * Callbacks for AdPrompts.
     */
    public interface AdPromptCallbackListener {
        /**
         * This event is fired after the AdPrompt is loaded and ready to be displayed.
         */
        public void adPromptLoaded(AdPrompt adPrompt);

        /**
         * This event is fired after the AdPrompt is displayed.
         */
        public void adPromptDisplayed(AdPrompt adPrompt);

        /**
         * This event is fired if the AdPrompt failed to load.
         */
        public void adPromptError(AdPrompt adPrompt, String error);

        /**
         * This event is fired if the AdPrompt was closed.
         * @param didAccept true if user pressed the call to action button, false otherwise
         */
        public void adPromptClosed(AdPrompt adPrompt, boolean didAccept);
    }
}
