package com.snakk.adview.track;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;

import com.snakk.adview.AdLog;
import com.snakk.adview.Utils;

/**
 * Conversion and Installation Tracker. Sends a notification to Phunware servers
 * with UDID, UA, and Package Name
 */
public class InstallTracker {
	private Context mContext;
	private String mPackageName;
	private String mOfferId;
	private String ua = null;	

	static private String TRACK_HOST = "a.snakkads.com";
	static private String TRACK_HANDLER = "/adconvert.php";

	private static InstallTracker mInstance = null;

	private AdLog adLog = new AdLog(this);
	
	private InstallTracker() {
		super();
	}

	public static InstallTracker getInstance() {
		if (mInstance == null) {
			mInstance = new InstallTracker();
		}
		return (mInstance);
	}

	/**
	 * Send Install Notification To Phunware
	 * 
	 * @param context
	 *            - The reference to the context of Activity
	 */
	public void reportInstall(Context context) {
		reportInstall(context, null);
	}

	/**
	 * Send Install Notification To Phunware
	 * 
	 * @param context
	 *            - The reference to the context of Activity
	 * 
	 * @param offer
	 *            - The referer to attribute the install to
	 */
	public void reportInstall(Context context, String offer) {
		if (context == null) {
			return;
		}

		mContext = context;
		mOfferId = offer;
		mPackageName = mContext.getPackageName();

		SharedPreferences settings = mContext.getSharedPreferences("phunwareSettings", 0);
		if (settings.getBoolean(mPackageName + " installed", false) == false) {
			ua = Utils.getUserAgentString(mContext);
			new Thread(mTrackInstall).start();
		} else {
			adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "InstallTracker", "Install already tracked");
		}
	}

	private Runnable mTrackInstall = new Runnable() {
		public void run() {
			StringBuilder sz = new StringBuilder("http://" + TRACK_HOST + TRACK_HANDLER);
			sz.append("?pkg=" + mPackageName);

			if (mOfferId != null) {
				try {
					sz.append("&offer=" + URLEncoder.encode(mOfferId, "UTF-8"));
				} catch (Exception e) {
				}
			}

			// Lookup UDID
			String deviceIdMD5 = Utils.getDeviceIdMD5(mContext);
			sz.append("&udid=" + deviceIdMD5);
			
			// User Agent
			//
			if (ua != null) {
				try {
					sz.append("&ua=" + URLEncoder.encode(ua, "UTF-8"));
				} catch (Exception e) {
				}
			}

			String url = sz.toString();
			adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "InstallTracker", "Install track: " + url);

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException e) {
				// Just fail silently. We'll try the next time the app opens
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "InstallTracker", "Install track failed: ClientProtocolException (no signal?)");
				return;
			} catch (IOException e) {
				// Just fail silently. We'll try the next time the app opens
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "InstallTracker", "Install track failed: IOException (no signal?)");
				return;
			}

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "InstallTracker", "Install track failed: Status code != 200");
				return;
			}

			HttpEntity entity = response.getEntity();
			if (entity == null || entity.getContentLength() == 0) {
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "InstallTracker", "Install track failed: Response was empty");
				return;
			}

			// If we made it here, the request has been tracked
			adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "InstallTracker", "Install track successful");
			SharedPreferences.Editor editor = mContext.getSharedPreferences("phunwareSettings", 0)
					.edit();
			editor.putBoolean(mPackageName + " installed", true).commit();
		}
	};
	
	/**
	 * Set log level:<br>
	 * {@link com.snakk.adview.AdLog#LOG_LEVEL_NONE}<br>
	 * {@link com.snakk.adview.AdLog#LOG_LEVEL_1}<br>
	 * {@link com.snakk.adview.AdLog#LOG_LEVEL_2}<br>
	 * {@link com.snakk.adview.AdLog#LOG_LEVEL_3}<br>
	 *
	 * @param logLevel
	 */
	public void setLogLevel(int logLevel) {
		adLog.setLogLevel(logLevel);
	}
}
