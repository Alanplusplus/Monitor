package com.os.logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.monitor.greendao.DaoMaster;
import com.monitor.greendao.DaoSession;
import com.monitor.greendao.SmsEntity;
import com.monitor.greendao.SmsEntityDao;

/**
 * Created by Alan on 16/5/18.
 */
public class DBHelper {
    private static DBHelper sHelper;

    private DaoSession mDaoSession;

    private DBHelper(){

    }

    public static DBHelper getInstance(){
        if (sHelper == null){
            sHelper = new DBHelper();
        }
        return sHelper;
    }

    public void init(Context context){
        MyOpenHelper helper = new MyOpenHelper(context,"monitor-db",null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession(){
        return mDaoSession;
    }

}

class MyOpenHelper extends DaoMaster.OpenHelper{

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 4){
            SmsEntityDao.dropTable(db,true);
            SmsEntityDao.createTable(db,false);
        }
    }
}
