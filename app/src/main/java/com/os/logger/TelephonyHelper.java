package com.os.logger;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Alan on 16/5/16.
 */
public class TelephonyHelper {
    public static String getDeviceId(Context context){
        TelephonyManager manager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }
}
