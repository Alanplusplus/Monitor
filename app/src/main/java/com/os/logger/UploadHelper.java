package com.os.logger;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Alan on 16/5/12.
 */
public class UploadHelper {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static UploadHelper sInstance;
    private HashSet<Call> mFormCalls;

    private UploadHelper() {

    }

    public static UploadHelper getInstance() {
        if (sInstance == null) {
            sInstance = new UploadHelper();
        }
        return sInstance;
    }

    public void upload(String url, String json, Callback callback) {
        OkHttpClient client = SharedHttpClient.getClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void uploadForm(String url, File file, HashMap<String, String> args, Callback callback) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Builder mb = new MultipartBody.Builder();
        mb.setType(MediaType.parse("multipart/form-data"));
        if (args != null) {
            Set<String> keys = args.keySet();
            if (keys.size() > 0) {
                for (String key : keys) {
                    mb.addFormDataPart(key, args.get(key));
                }
            }
        }
        mb.addFormDataPart("file", file.getName(), fileBody);

        Request request = new Request.Builder()
                .url(url)
                .post(mb.build())
                .build();

        OkHttpClient client = SharedHttpClient.getClient();
        Call call = client.newCall(request);
        addFormCall(call);
        call.enqueue(callback);
    }

    private void addFormCall(Call call) {
        if (mFormCalls == null) {
            mFormCalls = new HashSet<>();
        }
        mFormCalls.add(call);
        LogUtil.e("addFormCall:" + mFormCalls.size());
    }

    public void removeFormCall(Call call) {
        if (mFormCalls == null) {
            return;
        }

        mFormCalls.remove(call);
        LogUtil.e("removeFormCall:" + mFormCalls.size());
    }

    public void cancelAllCalls() {
        if (mFormCalls == null) {
            return;
        }
        LogUtil.e("cancelAllCalls:" + mFormCalls.size());
        for (Call call : mFormCalls) {
            call.cancel();
        }
        mFormCalls.clear();
        mFormCalls = null;
    }
}
