package com.snakk.vastsdk;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


public class TVASTAdsRequest {

    protected static final String SERVER_BASE_URL = "http://r.snakkads.com";

    private Context mContext;
    private String mAdTagUrl;
    private TVASTAdType mAdType;
    private ArrayList<TVASTCompanionAdSlot> mCompanionSlots;

    private Map<String, String> mParameters = Collections.synchronizedMap(new HashMap<String, String>());
    private Map<String, String> mCustomParameters = Collections.synchronizedMap(new HashMap<String, String>());

    private static final String PARAMETER_ZONE = "zone";
    private static final String PARAMETER_ADTYPE = "adtype";
    private static final String PARAMETER_USER_AGENT = "ua";
    private static final String PARAMETER_LATITUDE = "lat";
    private static final String PARAMETER_LONGITUDE = "long";
    private static final String PARAMETER_HEIGHT = "h";
    private static final String PARAMETER_WIDTH = "w";
    private static final String PARAMETER_CONNECTION_SPEED = "connection_speed";
    private static final String PARAMETER_LANGUAGES = "languages";
    private static final String PARAMETER_CARRIER = "carrier";
    public final static String PARAMETER_DEVICE_ID = "udid";

    private String mAdServerURL = "http://r.snakkads.com/adrequest.php";

    public TVASTAdsRequest(String zone) {
        setZone(zone);
        mContext = null;
        mCompanionSlots = new ArrayList<TVASTCompanionAdSlot>();

        mAdTagUrl = null;
        mAdType = TVASTAdType.VIDEO;
        mCompanionSlots = null;
    }

    public void initDefaultParameters(Context context) {
        String deviceIdMD5 = TVASTUtils.getDeviceIdMD5(context);
        String carrierName = TVASTUtils.getCarrier(context);
        String ua = TVASTUtils.getUserAgentString(context);

        if ((deviceIdMD5 != null) && (deviceIdMD5.length() > 0)) {
            mParameters.put(PARAMETER_DEVICE_ID, deviceIdMD5);
        }

        mParameters.put("format", "vast");
        mParameters.put("sdk", "android-v" + TVASTAd.VERSION);
        mParameters.put(PARAMETER_CARRIER, carrierName);
        mParameters.put(PARAMETER_LANGUAGES, Locale.getDefault().getLanguage());
        mParameters.put(PARAMETER_USER_AGENT, ua);
    }

    /**
     * Get URL of ad server.
     *
     * @return
     */
    public synchronized String getAdserverURL() {
        return mAdServerURL;
    }

    /**
     * Overrides the URL of ad server.
     *
     * @param adserverURL
     */
    public synchronized void setAdserverURL(String adserverURL) {
        if ((adserverURL != null) && (adserverURL.length() > 0)) {
            this.mAdServerURL = adserverURL;
        }
    }

    /**
     * Optional. Set the browser user agent of the device making the request.
     *
     * @param ua
     * @return
     */
    public TVASTAdsRequest setUa(String ua) {
        if (ua != null) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_USER_AGENT, ua);
            }
        }
        return this;
    }

    /**
     * Required. Set the id of the zone of publisher site.
     *
     * @param zone
     * @return
     */
    public TVASTAdsRequest setZone(String zone) {
        if (zone != null) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_ZONE, zone);
            }
        }
        return this;
    }

    /**
     * Required. Set the adtype of the advertise.
     *
     * @param adtype
     * @return
     */
    public TVASTAdsRequest setAdtype(String adtype) {
        if (adtype != null) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_ADTYPE, adtype);
            }
        }
        return this;
    }

    /**
     * Optional. Set Latitude.
     *
     * @param latitude
     * @return
     */
    public TVASTAdsRequest setLatitude(String latitude) {
        if (latitude != null) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_LATITUDE, latitude);
            }
        }
        return this;
    }

    /**
     * Optional. Set Longitude.
     *
     * @param longitude
     * @return
     */
    public TVASTAdsRequest setLongitude(String longitude) {
        if (longitude != null) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_LONGITUDE, longitude);
            }
        }
        return this;
    }

    public TVASTAdsRequest setHeight(Integer height) {
        if ((height != null) && (height > 0)) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_HEIGHT, String.valueOf(height));
            }
        }
        return this;
    }

    public Integer getHeight() {
        synchronized (mParameters) {
            String height = mParameters.get(PARAMETER_HEIGHT);
            return getIntParameter(height);
        }
    }

    public TVASTAdsRequest setWidth(Integer width) {
        if ((width != null) && (width > 0)) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_WIDTH, String.valueOf(width));
            }
        }
        return this;
    }

    public Integer getWidth() {
        synchronized (mParameters) {
            String width = mParameters.get(PARAMETER_WIDTH);
            return getIntParameter(width);
        }
    }

    /**
     * Optional. Set connection speed. 0 - low (gprs, edge), 1 - fast (3g,
     * wifi).
     *
     * @param connectionSpeed
     * @return
     */
    public TVASTAdsRequest setConnectionSpeed(Integer connectionSpeed) {
        if (connectionSpeed != null) {
            synchronized (mParameters) {
                mParameters.put(PARAMETER_CONNECTION_SPEED, String.valueOf(connectionSpeed));
            }
        }
        return this;
    }

    public String getAdtype() {
        synchronized (mParameters) {
            return mParameters.get(PARAMETER_ADTYPE);
        }
    }

    public String getUa() {
        synchronized (mParameters) {
            return mParameters.get(PARAMETER_USER_AGENT);
        }
    }

    public String getZone() {
        synchronized (mParameters) {
            return mParameters.get(PARAMETER_ZONE);
        }
    }

    public String getLatitude() {
        synchronized (mParameters) {
            return mParameters.get(PARAMETER_LATITUDE);
        }
    }

    public String getLongitude() {
        synchronized (mParameters) {
            return mParameters.get(PARAMETER_LONGITUDE);
        }
    }

    public Integer getConnectionSpeed() {
        synchronized (mParameters) {
            String connectionSpeed = mParameters.get(PARAMETER_CONNECTION_SPEED);
            return getIntParameter(connectionSpeed);
        }
    }

    /**
     * Optional. Set Custom parameters.
     *
     * @param cParams a map containing parameters to add.  To clear out existing params, set cParams to null
     */
    public void setCustomParameters(Map<String, String> cParams) {
        if (cParams != null) {
            mCustomParameters.putAll(cParams);
        } else {
            mCustomParameters.clear();
        }
    }

    public Map<String, String> getCustomParameters() {
        return mCustomParameters;
    }

    private Integer getIntParameter(String stringValue) {
        if (stringValue != null) {
            return Integer.parseInt(stringValue);
        } else {
            return null;
        }
    }

    /**
     * Creates URL with given parameters.
     *
     * @return
     * @throws IllegalStateException if all the required parameters are not present.
     */
    public synchronized String createURL() throws IllegalStateException {
        return this.toString();
    }

    public synchronized String toString() {
        StringBuilder builderToString = new StringBuilder();
        String adserverURL = this.mAdServerURL + "?";
        builderToString.append(adserverURL);
        appendParameters(builderToString, mParameters);
        appendParameters(builderToString, mCustomParameters);

        String url = builderToString.toString();
        return url; // builderToString.toString().equals(adserverURL)
        // ? this.adserverURL :
        // builderToString.toString();
    }

    private void appendParameters(StringBuilder builderToString, Map<String, String> parameters) {

        if (parameters != null) {
            synchronized (parameters) {
                Set<String> keySet = parameters.keySet();

                for (Iterator<String> parameterNames = keySet.iterator(); parameterNames.hasNext(); ) {
                    String param = parameterNames.next();
                    String value = parameters.get(param);

                    if (value != null) {
                        try {
                            builderToString.append("&" + URLEncoder.encode(param, "UTF-8") + "="
                                    + URLEncoder.encode(value, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Snakk", "An error occured", e);
                        }
                    }
                }
            }
        }
    }


    public String getRequestParameter(String key) {
        String parameter = mParameters.get(key);
        return parameter;
    }

    public Context getUserRequestContext() {
        return mContext;
    }

	/*
    public ArrayList<TVASTCompanionAdSlot> getCompanionAdSlots() {
		return mCompanionSlots;
	}
	
	public void setCompanions(ArrayList<TVASTCompanionAdSlot> companions) {
		mCompanionSlots.clear();
		mCompanionSlots.addAll(companions);
	}
	*/

    public void setRequestParameter(String key, String value) {
        mCustomParameters.put(key, value);
    }

    public void setUserRequestContext(Context context) {
        mContext = context;
    }

	/*
	public String getAdTagUrl() {
		return mAdTagUrl;
	}
	
	public void setAdTagUrl(String adTagUrl) {
		mAdTagUrl = adTagUrl;
	}
	
	public TVASTAdType getAdType() {
		return mAdType;
	}
	
	public void setAdType(TVASTAdType adType) {
		mAdType = adType;
	}
	*/
}

