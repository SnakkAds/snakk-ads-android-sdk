package com.snakk.vastsdk;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class TVASTUtils {

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
            return TVASTUtils.byteArrayToHexString(messageDigest);
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

    public static String getDeviceId(Context context) {
        // Interesting discussion about getting a DeviceId
        // http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();
            if (deviceId == null) {
                deviceId = TVASTUtils.id(context);
            }
//			return Utils.md5(deviceId);
            return deviceId;
        } catch (SecurityException e) {
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

    public static String getDeviceIdMD5(Context context) {
        String udid = getDeviceId(context);
        if (udid != "unknown") {
            udid = TVASTUtils.md5(udid);
        }
        return udid;
    }

    public static String getCarrier(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }
}
