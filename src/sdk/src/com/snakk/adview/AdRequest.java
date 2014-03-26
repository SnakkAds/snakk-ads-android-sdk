package com.snakk.adview;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import android.content.Context;
import android.util.Log;

public class AdRequest {
    public static final String TAG = "Snakk";

    private Map<String, String> parameters = new HashMap<String, String>();
    private static final String PARAMETER_ZONE = "zone";
    private static final String PARAMETER_ADTYPE = "adtype";
    private static final String PARAMETER_USER_AGENT = "ua";
    private static final String PARAMETER_LATITUDE = "lat";
    private static final String PARAMETER_LONGITUDE = "long";
    private static final String PARAMETER_BACKGROUND = "paramBG";
    private static final String PARAMETER_LINK = "paramLINK";
    private static final String PARAMETER_MIN_SIZE_X = "min_size_x";
    private static final String PARAMETER_MIN_SIZE_Y = "min_size_y";
    private static final String PARAMETER_SIZE_X = "size_x";
    private static final String PARAMETER_SIZE_Y = "size_y";
    private static final String PARAMETER_HEIGHT = "h";
    private static final String PARAMETER_WIDTH = "w";
    private static final String PARAMETER_CONNECTION_SPEED = "connection_speed";
    private static final String PARAMETER_LANGUAGES = "languages";
    private static final String PARAMETER_CARRIER_NAME = "carrier";
    private static final String PARAMETER_CARRIER_ID = "carrier_id";
    public final static String PARAMETER_DEVICE_ID = "udid";

    private String adserverURL = "http://r.snakkads.com/adrequest.php";
//    private String adserverURL = "http://dev.snakkads.com/~npenteado/adrequest.php";

    private Map<String, String> customParameters = new HashMap<String, String>();

    private AdLog adLog;

    public AdRequest(AdLog adLog) {
        this.adLog = adLog;
    }

    public AdRequest(String zone) {
        setZone(zone);
        adLog = new AdLog(this);
    }

    public void initDefaultParameters(Context context) {
        String deviceIdMD5 = Utils.getDeviceIdMD5(context);
        String carrierName = Utils.getCarrierName(context);
        String carrierId = Utils.getCarrierId(context);
        String ua = Utils.getUserAgentString(context);

        adLog.log(AdLog.LOG_LEVEL_2, AdLog.LOG_TYPE_INFO, "deviceIdMD5", deviceIdMD5);
        if ((deviceIdMD5 != null) && (deviceIdMD5.length() > 0)) {
            parameters.put(PARAMETER_DEVICE_ID, deviceIdMD5);
        }

        parameters.put("format", "json");
        parameters.put("sdk", "android-v" + AdViewCore.VERSION);
        parameters.put(PARAMETER_CARRIER_NAME, carrierName);
        parameters.put(PARAMETER_CARRIER_ID, carrierId);
        parameters.put(PARAMETER_LANGUAGES, Locale.getDefault().getLanguage());
        parameters.put(PARAMETER_USER_AGENT, ua);
    }

    /**
     * Get URL of ad server.
     *
     * @return
     */
    public synchronized String getAdserverURL() {
        return adserverURL;
    }

    /**
     * Overrides the URL of ad server.
     *
     * @param adserverURL
     */
    public synchronized void setAdserverURL(String adserverURL) {
        if ((adserverURL != null) && (adserverURL.length() > 0)) {
            this.adserverURL = adserverURL;
        }
    }

    /**
     * Optional. Set the browser user agent of the device making the request.
     *
     * @param ua
     * @return
     */
    public AdRequest setUa(String ua) {
        if (ua != null) {
            parameters.put(PARAMETER_USER_AGENT, ua);
        }
        return this;
    }

    /**
     * Required. Set the id of the zone of publisher site.
     *
     * @param zone
     * @return
     */
    public final AdRequest setZone(String zone) {
        if (zone != null) {
            parameters.put(PARAMETER_ZONE, zone);
        }
        return this;
    }

    /**
     * Required. Set the adtype of the advertise.
     *
     * @param adtype
     * @return
     */
    public AdRequest setAdtype(String adtype) {
        if (adtype != null) {
            parameters.put(PARAMETER_ADTYPE, adtype);
        }
        return this;
    }

    /**
     * Optional. Set Latitude.
     *
     * @param latitude
     * @return
     */
    public AdRequest setLatitude(String latitude) {
        if (latitude != null) {
            parameters.put(PARAMETER_LATITUDE, latitude);
        }
        return this;
    }

    /**
     * Optional. Set Longitude.
     *
     * @param longitude
     * @return
     */
    public AdRequest setLongitude(String longitude) {
        if (longitude != null) {
            parameters.put(PARAMETER_LONGITUDE, longitude);
        }
        return this;
    }

    /**
     * Optional. Set Background color in borders.
     *
     * @param paramBG
     * @return
     */
    public AdRequest setParamBG(String paramBG) {
        if (paramBG != null) {
            parameters.put(PARAMETER_BACKGROUND, paramBG);
        }
        return this;
    }

    /**
     * Optional. Set Text color.
     *
     * @param paramLINK
     * @return
     */
    public AdRequest setParamLINK(String paramLINK) {
        if (paramLINK != null) {
            parameters.put(PARAMETER_LINK, paramLINK);
        }
        return this;
    }

    /**
     * @deprecated
     * Optional. Set minimum width of advertising.
     *
     * @param minSizeX
     * @return
     */
    @Deprecated
    public AdRequest setMinSizeX(Integer minSizeX) {
        if ((minSizeX != null) && (minSizeX > 0)) {
            parameters.put(PARAMETER_MIN_SIZE_X, String.valueOf(minSizeX));
        }
        return this;
    }

    /**
     * @deprecated
     * Optional. Set minimum height of advertising.
     *
     * @param minSizeY
     * @return
     */
    @Deprecated
    public AdRequest setMinSizeY(Integer minSizeY) {
        if ((minSizeY != null) && (minSizeY > 0)) {
            parameters.put(PARAMETER_MIN_SIZE_Y, String.valueOf(minSizeY));
        }
        return this;
    }

    /**
     * @deprecated
     * Optional. Set maximum width of advertising.
     *
     * @param sizeX
     * @return
     */
    @Deprecated
    public AdRequest setSizeX(Integer sizeX) {
        if ((sizeX != null) && (sizeX > 0)) {
            parameters.put(PARAMETER_SIZE_X, String.valueOf(sizeX));
        }
        return this;
    }

    /**
     * @deprecated
     * Optional. Set maximum height of advertising.
     *
     * @param sizeY
     * @return
     */
    @Deprecated
    public AdRequest setSizeY(Integer sizeY) {
        if ((sizeY != null) && (sizeY > 0)) {
            parameters.put(PARAMETER_SIZE_Y, String.valueOf(sizeY));
        }
        return this;
    }

    public AdRequest setHeight(Integer height) {
        if ((height != null) && (height > 0)) {
            parameters.put(PARAMETER_HEIGHT, String.valueOf(height));
        }
        return this;
    }

    public Integer getHeight() {
        String height = parameters.get(PARAMETER_HEIGHT);
        return getIntParameter(height);
    }

    public AdRequest setWidth(Integer width) {
        if ((width != null) && (width > 0)) {
            parameters.put(PARAMETER_WIDTH, String.valueOf(width));
        }
        return this;
    }

    public Integer getWidth() {
        String width = parameters.get(PARAMETER_WIDTH);
        return getIntParameter(width);
    }

    /**
     * Optional. Set connection speed. 0 - low (gprs, edge), 1 - fast (3g,
     * wifi).
     *
     * @param connectionSpeed
     * @return
     */
    public AdRequest setConnectionSpeed(Integer connectionSpeed) {
        if (connectionSpeed != null) {
            parameters.put(PARAMETER_CONNECTION_SPEED, String.valueOf(connectionSpeed));
        }
        return this;
    }

    public String getAdtype() {
        return parameters.get(PARAMETER_ADTYPE);
    }

    public String getUa() {
        return parameters.get(PARAMETER_USER_AGENT);
    }

    public String getZone() {
        return parameters.get(PARAMETER_ZONE);
    }

    public String getLatitude() {
        return parameters.get(PARAMETER_LATITUDE);
    }

    public String getLongitude() {
        return parameters.get(PARAMETER_LONGITUDE);
    }

    public String getParamBG() {
        return parameters.get(PARAMETER_BACKGROUND);
    }

    public String getParamLINK() {
        return parameters.get(PARAMETER_LINK);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Integer getMinSizeX() {
        String minSizeX = parameters.get(PARAMETER_MIN_SIZE_X);
        return getIntParameter(minSizeX);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Integer getMinSizeY() {
        String minSizeY = parameters.get(PARAMETER_MIN_SIZE_Y);
        return getIntParameter(minSizeY);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Integer getSizeX() {
        String sizeX = parameters.get(PARAMETER_SIZE_X);
        return getIntParameter(sizeX);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Integer getSizeY() {
        String sizeY = parameters.get(PARAMETER_SIZE_Y);
        return getIntParameter(sizeY);
    }

    public Integer getConnectionSpeed() {
        String connectionSpeed = parameters.get(PARAMETER_CONNECTION_SPEED);
        return getIntParameter(connectionSpeed);
    }

    /**
     * @deprecated use setCustomParameters(Map<String, String> cParams)
     * Optional. Set Custom parameters.
     *
     * @param cParams
     * @return
     */
    @Deprecated
    public void setCustomParameters(Hashtable<String, String> cParams) {
//        customParameters.putAll(cParams); // this throws a NPE
        for (String key : cParams.keySet()) {
            customParameters.put(key, cParams.get(key));
        }
    }

    /**
     * Optional. Set Custom parameters.
     *
     * @param cParams a map containing parameters to add.  To clear out existing params, set cParams to null
     */
    public void setCustomParameters(Map<String, String> cParams) {
        if (cParams != null) {
            customParameters.putAll(cParams);
        }
        else {
            customParameters.clear();
        }
    }

    public Map<String, String> getCustomParameters() {
        return customParameters;
    }

    private static Integer getIntParameter(String stringValue) {
        if (stringValue != null) {
            return Integer.parseInt(stringValue);
        } else {
            return null;
        }
    }

    /**
     * Creates URL with given parameters.
     *
     * @return URL string representing this ad request
     * @throws IllegalStateException
     *             if all the required parameters are not present.
     */
    public synchronized String createURL() throws IllegalStateException {
        return this.toString();
    }

    public synchronized String toString() {
        StringBuilder builderToString = new StringBuilder();
        String adserverURL = this.adserverURL + '?';
        builderToString.append(adserverURL);
        appendParameters(builderToString, parameters);
        appendParameters(builderToString, customParameters);

        String url = builderToString.toString();
        return url; // builderToString.toString().equals(adserverURL)
                                            // ? this.adserverURL :
                                            // builderToString.toString();
    }

    private static void appendParameters(StringBuilder builderToString, Map<String, String> parameters) {

        if (parameters != null) {
            Set<String> keySet = parameters.keySet();

            for (Map.Entry<String, String> parmEntry : parameters.entrySet()) {
                String value = parmEntry.getValue();

                if (value != null) {
                    try {
                        builderToString.append('&')
                                .append(URLEncoder.encode(parmEntry.getKey(), "UTF-8"))
                                .append('=').append(URLEncoder.encode(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "An error occurred", e);
                    }
                }
            }
        }
    }

}
