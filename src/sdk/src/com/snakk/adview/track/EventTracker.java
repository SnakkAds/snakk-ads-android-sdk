package com.snakk.adview.track;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

import com.snakk.adview.AdLog;
import com.snakk.adview.Utils;

/**
 * Application Event Tracker. Sends a notification to PW servers with UDID,
 * UA, and Package Name
 */
public class EventTracker {
	private Context mContext;
	private String mPackageName;
	private String mEvent;
	private String ua = null;

	static private String TRACK_HOST = "a.snakkads.com";
	static private String TRACK_HANDLER = "/trackevent.php";

	private static EventTracker mInstance = null;
	
	private AdLog adLog = new AdLog(this);

	private EventTracker() {
		super();
	}

	public static EventTracker getInstance() {
		if (mInstance == null) {
			mInstance = new EventTracker();
		}
		return (mInstance);
	}

	/**
	 * Send Event Notification To Phunware
	 * 
	 * @param context
	 *            - The reference to the context of Activity
	 * 
	 * @param event
	 *            - The tag string of the event that occurred
	 */
	public void reportEvent(Context context, String event) {
		if (context == null) {
			return;
		}

		mContext = context;
		mEvent = event;
		mPackageName = mContext.getPackageName();
		if (ua == null)
			ua = Utils.getUserAgentString(mContext);

		new Thread(mTrackEvent).start();
	}

	private Runnable mTrackEvent = new Runnable() {
		public void run() {
			StringBuilder sz = new StringBuilder("http://" + TRACK_HOST + TRACK_HANDLER);
			sz.append("?pkg=" + mPackageName);

			if (mEvent == null || mEvent == "") {
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "EventTracker", "Event track failed: No Event Tag Defined");
				return;
			}

			try {
				sz.append("&event=" + URLEncoder.encode(mEvent, "UTF-8"));
			} catch(Exception e){
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
			adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "EventTracker", "Event track: " + url);

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException e) {
				// Just fail silently. We'll try the next time the app opens
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "EventTracker", "Event track failed: ClientProtocolException (no signal?)");
				return;
			} catch (IOException e) {
				// Just fail silently. We'll try the next time the app opens
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "EventTracker", "Event track failed: IOException (no signal?)");
				return;
			}

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "EventTracker", "Event track failed: Status code != 200");
				return;
			}

			/* TODO: remove:
			HttpEntity entity = response.getEntity();
			if (entity == null || entity.getContentLength() == 0) {
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "EventTracker", "Event track failed: Response was empty");
				return;
			}
			*/

			// If we made it here, the request has been tracked
			adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "EventTracker", "Event track successful");
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
