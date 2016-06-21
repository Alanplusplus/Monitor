package com.os.logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by Alan on 16/5/11.
 */
public class PreferenceUtil {
    public static final String KEY_SMS_LATEST_DATE = "sms_latest_date";
    public static final String KEY_USER_NAME = "user_name";

    private PreferenceUtil(){

    }

    public static void saveValue(Context context, String key, long value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public static void saveValue(Context context, String key, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static long getLong(Context context, String key, long defValue){
        return getSharedPreferences(context).getLong(key,defValue);
    }

    public static String getString(Context context, String key){
        return getSharedPreferences(context).getString(key,"NotSet");
    }

    private static SharedPreferences getSharedPreferences(Context context){
       return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
