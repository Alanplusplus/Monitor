package com.os.logger;

import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.marswin89.marsdaemon.PackageUtils;

/**
 * Created by Alan on 16/5/3.
 */
public class LogApplication extends DaemonApplication{
    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.
                DaemonConfiguration("com.monitor:remote",ReportService.class.getCanonicalName(),
                RemoteReceiver.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.
                DaemonConfiguration("com.monitor:guard",GuardService.class.getCanonicalName(),
                GuardReceiver.class.getCanonicalName());

        return new DaemonConfigurations(configuration1,configuration2);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DBHelper.getInstance().init(getApplicationContext());

        PackageUtils.setComponentDefault(getApplicationContext(),WakeReceiver.class.getCanonicalName());
    }
}
