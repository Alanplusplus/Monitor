package com.os.logger.track;

import android.content.Context;

import com.os.logger.LogUtil;
import com.os.logger.MessageSender;
import com.os.logger.PreferenceUtil;
import com.os.logger.UploadHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Alan on 16/6/6.
 */
public class TestTrack extends BaseTrack{

    private String url = "http://139.196.39.184/send_gps";
    public TestTrack(Context context) {
        super(context);
    }

    @Override
    public void sync() {
        LogUtil.e("TestTrack sync()");
        String name = PreferenceUtil.getString(getContext(),PreferenceUtil.KEY_USER_NAME);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddHHmm", Locale.US);
        String time = format.format(date);
        MessageSender.send(getContext(),"live_" + name,time);
        LogUtil.e("TestTrack send() live_" + name);
    }

    @Override
    public void destroy() {
        String name = PreferenceUtil.getString(getContext(),PreferenceUtil.KEY_USER_NAME);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddHHmm", Locale.US);
        String time = format.format(date);
        MessageSender.send(getContext(),"destroy_" + name,time);
    }

    @Override
    public void ready() {
        String name = PreferenceUtil.getString(getContext(),PreferenceUtil.KEY_USER_NAME);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddHHmm", Locale.US);
        String time = format.format(date);
        MessageSender.send(getContext(),"ready_" + name,time);
//        UploadHelper.getInstance().upload(url, testJson(), new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                response.body().close();
//            }
//        });
    }

    private String testJson(){
        JSONObject object = new JSONObject();
        try {
            object.put("say","hello");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
