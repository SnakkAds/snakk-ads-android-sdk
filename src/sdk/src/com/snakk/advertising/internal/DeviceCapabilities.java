package com.snakk.advertising.internal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.View;
import com.snakk.adview.Utils;

import java.net.URL;
import java.util.List;

public final class DeviceCapabilities {

    /**
     * Check to see if device has a dialer program
     * @param context app context
     * @return true if Intent.ACTION_DIAL should succeed, false otherwise
     */
    public static boolean canMakePhonecalls(Context context) {

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        List<ResolveInfo> callAppsList = context.getPackageManager().queryIntentActivities(callIntent, 0);
        return callAppsList != null && !callAppsList.isEmpty();
    }


    /**
     * Check to see if the device can send SMS's and that app has permission to do so.
     * @param context app context
     * @return true if app has permissions and device is able to send SMS's, false otherwise
     */
    public static boolean canSendSMS(Context context) {
        //TODO implement me!
//        SmsManager manager = SmsManager.getDefault();
//        Utils.hasPermission(context, Manifest.permission.WRITE_SMS);
        return false;
    }

    public static boolean canCreateCalendarEvents(Context context) {
        //TODO implement me!
        return true;
    }
}
