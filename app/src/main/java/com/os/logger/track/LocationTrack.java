package com.os.logger.track;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.monitor.greendao.LocationEntity;
import com.monitor.greendao.LocationEntityDao;
import com.os.logger.DBHelper;
import com.os.logger.LogUtil;
import com.os.logger.MessageSender;
import com.os.logger.PreferenceUtil;
import com.os.logger.TelephonyHelper;
import com.os.logger.UploadHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Alan on 16/5/12.
 */
public class LocationTrack extends BaseTrack {

    private static String sUrl = "http://139.196.39.184/send_gps";

    public LocationTrack(Context context) {
        super(context);
    }

    private void getLocationByGaode() {
        AMapLocationClient client = new AMapLocationClient(getContext().getApplicationContext());
        AMapLocationListener listener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        dealLocation(aMapLocation);
                    } else {
                        LogUtil.e(aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        client.setLocationListener(listener);

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setNeedAddress(true);
//        option.setInterval(30 * 60 * 1000);
        option.setOnceLocation(true);
//        option.setOnceLocation(true);
        client.setLocationOption(option);


        client.startLocation();
    }

    private void dealLocation(AMapLocation location) {
        if (location == null) {
            return;
        }

        reportOrSave(System.currentTimeMillis(), location);
//        JSONObject json = new JSONObject();
//        try {
//            json.put("time", location.getTime());
//            json.put("latitude", location.getLatitude());
//            json.put("longitude", location.getLongitude());
//            Bundle bundle = location.getExtras();
//            if (bundle != null && bundle.containsKey("desc")) {
//                json.put("desc", bundle.getString("desc"));
//            }
//            json.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        LogUtil.e(json.toString());
    }

    @Override
    public void sync() {
        if (!noNetConnection()) {
            report();
        }
        try {
            getLocationByGaode();
        } catch (Exception e) {
            //permission denied
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
//        mLocationClient.stopLocation();
    }

    @Override
    public void ready() {
        MessageSender.send(getContext(),"ready","locationTrack");
    }

    private void save(AMapLocation location, long updateTime) {
        LocationEntityDao dao = DBHelper.getInstance().getDaoSession().getLocationEntityDao();
        Bundle bundle = location.getExtras();
        String desc = null;
        if (bundle != null && bundle.containsKey("desc")) {
            desc = bundle.getString("desc");
        }

        LocationEntity entity = new LocationEntity(updateTime, location.getLatitude(),
                location.getLongitude(), desc);

        dao.insertOrReplace(entity);
        LogUtil.e("save AmapLocation");
    }

    private void report() {
        LocationEntityDao dao = DBHelper.getInstance().getDaoSession().getLocationEntityDao();
        List<LocationEntity> list = dao.queryBuilder()
                .orderDesc(LocationEntityDao.Properties.Time)
                .list();
        if (list == null || list.size() == 0) {
            return;
        }

        for (LocationEntity entity : list) {
            reportOrDoNothing(entity);
        }
    }

    private void reportOrDoNothing(final LocationEntity entity) {
        UploadHelper.getInstance().upload(sUrl, toJson(entity), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isSuccessFul(response)) {
                    delete(entity);
                }
            }
        });
    }

    private boolean isSuccessFul(Response response) {
        if (!response.isSuccessful()){
            response.body().close();
            return false;
        }
        String body = null;
        try {
            body = response.body().string();
            LogUtil.e("location:"+ body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body != null && !body.contains("errorno");
    }


    private String toJson(LocationEntity entity) {
        JSONObject json = new JSONObject();
        try {
            String userName = PreferenceUtil.getString(getContext(), PreferenceUtil.KEY_USER_NAME);
            json.put("username", userName);
            json.put("time", entity.getTime());
            json.put("latitude", entity.getLatitude());
            json.put("longitude", entity.getLongitude());
            json.put("desc", entity.getDesc());
            json.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(json.toString());
        return json.toString();
    }

    private String toJson(AMapLocation location, long updateTime) {
        JSONObject json = new JSONObject();
        try {
            String userName = PreferenceUtil.getString(getContext(), PreferenceUtil.KEY_USER_NAME);
            json.put("username", userName);
            json.put("time", updateTime);
            json.put("latitude", location.getLatitude());
            json.put("longitude", location.getLongitude());
            Bundle bundle = location.getExtras();
            if (bundle != null && bundle.containsKey("desc")) {
                json.put("desc", bundle.getString("desc"));
            }
            json.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e(json.toString());
        return json.toString();
    }


    private void delete(LocationEntity entity) {
        LocationEntityDao dao = DBHelper.getInstance().getDaoSession().getLocationEntityDao();
        dao.delete(entity);
    }

    private void reportOrSave(final long updateTime, final AMapLocation location) {
        LogUtil.e("reportOrSave");
        UploadHelper.getInstance().upload(sUrl, toJson(location, updateTime), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                save(location, updateTime);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.e("Got Response:" + response.code());
//                if (!response.isSuccessful()) {
//                    save(location, updateTime);
//                }
//                response.body().close();
                if (!isSuccessFul(response)){
                    save(location,updateTime);
                }
            }
        });
    }
}
