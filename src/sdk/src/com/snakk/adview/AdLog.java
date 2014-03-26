package com.snakk.adview;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class AdLog {

    /*
     * Where logLever can be one of:
     * AdLog.LOG_LEVEL_NONE	none
     * AdLog.LOG_LEVEL_1 		only errors
     * AdLog.LOG_LEVEL_2 		+warning
     * AdLog.LOG_LEVEL_3 		+server traffic
     */

    /**
     * none
     */
    public static final int LOG_LEVEL_NONE = 0;

    /**
     * only errors
     */
    public static final int LOG_LEVEL_1 = 1;
    /**
     * +warning
     */
    public static final int LOG_LEVEL_2 = 2;
    /**
     * +server traffic
     */
    public static final int LOG_LEVEL_3 = 3;

    public static final int LOG_TYPE_ERROR = 1;
    public static final int LOG_TYPE_WARNING = 2;
    public static final int LOG_TYPE_INFO = 3;

    private int currentLogLevel = 0;

    private Object object;

    private static int defaultLevel = LOG_LEVEL_NONE;

    public static void setDefaultLogLevel(int logLevel) {
        defaultLevel = logLevel;
    }

    /**
     * set log filename, i.e. "/sdcard/AdvLogs.txt"
     * @param fileName
     */
    public static void setFileLog(String fileName) {
        try {
            File filename = new File(fileName);
            if (filename.exists())
                filename.delete();
            filename.createNewFile();
            String cmd = "logcat -v time -f " + filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            Log.e("Snakk", "An error occured", e);
        }
    }

    public AdLog(Object object) {
        this.object = object;
        setLogLevel(defaultLevel);
    }

    public void log(int Level, int Type, String tag, String msg) {
        String resultTag = "[" + Integer.toHexString(object.hashCode()) + "]" + tag;

        if (Level <= currentLogLevel) {
            switch (Type) {
            case LOG_TYPE_ERROR:
                Log.e(resultTag, msg + ' ');
                break;
            case LOG_TYPE_WARNING:
                Log.w(resultTag, msg + ' ');
                break;
            default:
                Log.i(resultTag, msg + ' ');
            }
        }
    }

    public void setLogLevel(int logLevel) {
        currentLogLevel = logLevel;
        switch (logLevel) {
        case LOG_LEVEL_1:
            log(LOG_LEVEL_1, LOG_TYPE_INFO, "SetLogLevel", "LOG_LEVEL_1");
            break;
        case LOG_LEVEL_2:
            log(LOG_LEVEL_1, LOG_TYPE_INFO, "SetLogLevel", "LOG_LEVEL_2");
            break;
        case LOG_LEVEL_3:
            log(LOG_LEVEL_1, LOG_TYPE_INFO, "SetLogLevel", "LOG_LEVEL_3");
            break;
        default:
            log(LOG_LEVEL_1, LOG_TYPE_INFO, "SetLogLevel", "LOG_LEVEL_NONE");
        }
    }
}
