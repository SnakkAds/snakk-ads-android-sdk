package com.snakk.adview;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Utils {

    private static final String TAG = "Snakk";
    private static final Object lock = new Object();
    private static String userAgent = null;
	
	public static String scrape(String resp, String start, String stop) {
		int offset, len;
		if ((offset = resp.indexOf(start)) < 0)
			return "";
		if ((len = resp.indexOf(stop, offset + start.length())) < 0)
			return "";
		return resp.substring(offset + start.length(), len);
	}

	public static String md5(String data) {
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			digester.update(data.getBytes());
			byte[] messageDigest = digester.digest();
			return Utils.byteArrayToHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static String byteArrayToHexString(byte[] array) {
		StringBuffer hexString = new StringBuffer();
		for (byte b : array) {
			int intVal = b & 0xff;
			if (intVal < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(intVal));
		}
		return hexString.toString();
	}

    public static String getUserAgentString(Context context) {
        if (userAgent == null) {
            synchronized (lock) {
                try {
                    Constructor<WebSettings> constructor = WebSettings.class.getDeclaredConstructor(
                            Context.class, WebView.class);
                    constructor.setAccessible(true);
                    try {
                        WebSettings settings = constructor.newInstance(context, null);
                        userAgent = settings.getUserAgentString();
                    } finally {
                        constructor.setAccessible(false);
                    }
                } catch (Exception e) {
                    userAgent = new WebView(context).getSettings().getUserAgentString();
                }
            }
        }
        return userAgent;
    }

	private static String sID = null;
	private static final String INSTALLATION = "INSTALLATION";

	public synchronized static String id(Context context) {
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				// throw new RuntimeException(e);
				sID = "1234567890";
			}
		}
		return sID;
	}

	private static String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}
	
	public static String getDeviceId(Context context){
		// Interesting discussion about getting a DeviceId
		// http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = tm.getDeviceId();
			if (deviceId == null) {
				deviceId = Utils.id(context);
			}
//			return Utils.md5(deviceId);
			return deviceId;
		}
		catch(SecurityException e) {
			return "unknown";
		}
		// An alternate implementation that doesn't require the READ_PHONE_STATE permission
//        String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
//        if(deviceId != null) {
//        	deviceId = Utils.md5(deviceId);
//        }
//		
//        return deviceId;
	}
	
	public static String getDeviceIdMD5(Context context){
	    String udid = getDeviceId(context);
	    if(!"unknown".equals(udid)) {
	        udid = Utils.md5(udid);
	    }
	    return udid;
	}
	
	public static String getCarrierName(Context context) {
		TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getNetworkOperatorName();
	}

	public static String getCarrierId(Context context) {
		TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getNetworkOperator();
	}

    private static final Pattern QUESTION_MARK_PATTERN = Pattern.compile("\\?");
    private static final Pattern AMPERSTAND_PATTERN = Pattern.compile("&");
    private static final Pattern EQUALS_PATTERN = Pattern.compile("=");

    public static Map<String,String> parseUrlParams(String url) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        String parts[] = QUESTION_MARK_PATTERN.split(url, 2);
        if(parts.length > 1) {
            for(String p : AMPERSTAND_PATTERN.split(parts[1])) {
                String kv[] = EQUALS_PATTERN.split(p);
                String key = URLDecoder.decode(kv[0], "UTF-8");
                String val = URLDecoder.decode(kv[1], "UTF-8");
                params.put(key,val); // possible overwrite of value if duplicates exist in qs
            }
        }
        return params;
    }

    public static String appendUrlParams(Map<String, String> params) {
        final StringBuilder sb = new StringBuilder(params.size() * 6);
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()) {
            try {
                if (first) {
                    first = false;
                }
                else {
                    sb.append("&");
                }
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                String val = URLEncoder.encode(entry.getValue(), "UTF-8");
                sb.append(key).append("=").append(val);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Failed to url encode values: " + entry);
            }
        }
        return sb.toString();
    }

        private static String getResource(final Context ctx, final String name, final String resourceType) {
            String rsString = String.format("@%s/", resourceType);
            if(name == null || !name.startsWith(rsString)) {
            return name;
        }

        final String propName = name.substring(rsString.length());
        int resId = ctx.getResources().getIdentifier(propName, resourceType, ctx.getPackageName());
        if (resId > 0) {
            return ctx.getResources().getString(resId);
        }

        return null;
    }

    public static String getStringResource(final Context ctx, final String name) {
        return getResource(ctx, name, "string");
    }

    public static Integer getIntegerResource(final Context ctx, final String name, Integer defaultVal) {
        String res = getResource(ctx, name, "integer");
        if (res == null) {
            return defaultVal;
        }
        return Integer.valueOf(res);
    }

    public static boolean hasPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        int result = pm.checkPermission(permission, context.getPackageName());
        return (result == PackageManager.PERMISSION_GRANTED);
    }
}
