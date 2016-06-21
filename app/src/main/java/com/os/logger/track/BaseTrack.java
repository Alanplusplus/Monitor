package com.os.logger.track;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * Created by Alan on 16/5/9.
 */
public abstract class BaseTrack {

    private Context mContext;
    public BaseTrack(Context context){
        mContext = context;
    }

    protected Context getContext(){
        return mContext;
    }
    public abstract void sync();

    public abstract void destroy();

    public abstract void ready();

    protected boolean inWifi() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    protected boolean noNetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info == null;
    }
}
