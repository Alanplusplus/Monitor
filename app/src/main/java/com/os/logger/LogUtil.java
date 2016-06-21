package com.os.logger;

import android.util.Log;

import com.monitor.greendao.LogEntity;
import com.monitor.greendao.LogEntityDao;

/**
 * Created by Alan on 16/5/3.
 */
public class LogUtil {
    private LogUtil(){

    }

    public static void e(String message){
        if (BuildConfig.DEBUG){
            Log.e("report",message);
        }
    }

    public static void addLocalLog(String message){
        if (!BuildConfig.DEBUG){
            return;
        }
        LogEntityDao dao = DBHelper.getInstance().getDaoSession().getLogEntityDao();
        LogEntity entity = new LogEntity(System.currentTimeMillis(),message);
        dao.insertOrReplace(entity);
    }

}
