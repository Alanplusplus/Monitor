package com.os.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.marswin89.marsdaemon.PackageUtils;

/**
 * Created by Alan on 16/5/3.
 */
public class WakeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.e("receive:" + intent);
        context.startService(new Intent(context,ReportService.class));

        PackageUtils.setComponentDefault(context.getApplicationContext(),WakeReceiver.class.getCanonicalName());
    }
}
