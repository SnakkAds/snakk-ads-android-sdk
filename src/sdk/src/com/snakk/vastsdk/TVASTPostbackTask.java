package com.snakk.vastsdk;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TVASTPostbackTask extends AsyncTask<Integer, Integer, Object> {

    /**
     * Callbacks for TVASTPostbackTask.
     */
    public interface TVASTPostbackListener {
        /**
         * This event is fired after AlertAd is loaded and ready to be displayed.
         */
        public void onSuccess(String data);

        /**
         * This event is fired after AlertAd is displayed.
         */
        public void onFailure(Exception error);
    }

    private String mUrl;
    private TVASTPostbackListener mListener;

    public TVASTPostbackListener getListener() {
        return mListener;
    }

    public void setListener(TVASTPostbackListener listener) {
        mListener = listener;
    }

    private String postbackRequest(String url) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 8192);
        String responseValue = readInputStream(bufferedInputStream);
        bufferedInputStream.close();
        inputStream.close();
        return responseValue;
    }

    private static String readInputStream(BufferedInputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] buffer = new byte[8192];
        for (int n; (n = in.read(buffer)) != -1; ) {
            out.append(new String(buffer, 0, n));
        }
        return out.toString();
    }

    public TVASTPostbackTask(String url) {
        mUrl = url;
    }

    @Override
    protected Object doInBackground(Integer... params) {
        if (mUrl == null)
            return null;

        //Log.d("SnakkVASTSDK", mUrl);
        String data;
        try {
            data = postbackRequest(mUrl);
            return data;
        } catch (IOException e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object response) {
        //Log.d("SnakkVASTSDK", response);
        String error = null;

        if (response instanceof String) {
            try {
                JSONObject jsonObject = new JSONObject((String) response);

                if (jsonObject.has("error")) {
                    error = jsonObject.getString("error");
                    Exception e = new Exception(error);
                    if (mListener != null) {
                        mListener.onFailure(e);
                    }
                } else {
                    if (mListener != null) {
                        mListener.onSuccess((String) response);
                    }
                }
            } catch (JSONException jsonE) {
                if (mListener != null) {
                    mListener.onSuccess((String) response);
                }
            }
        } else if (response instanceof Exception) {
            if (mListener != null) {
                mListener.onFailure((Exception) response);
            }
        }
    }
}
