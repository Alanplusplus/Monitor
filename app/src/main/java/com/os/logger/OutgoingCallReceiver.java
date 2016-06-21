package com.os.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.os.logger.track.PhoneNumber;

/**
 * Created by Alan on 16/6/12.
 */
public class OutgoingCallReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_NEW_OUTGOING_CALL)){
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            PhoneNumber.setOutgoingNumber(number);
            LogUtil.addLocalLog("number in OutgoingCallReceiver:" + number);
        }
    }
}
