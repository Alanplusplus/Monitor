package com.os.logger;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Alan on 16/6/14.
 */
public class MessageSender {

    public static void send(Context context , String eventType, String msg){
        MobclickAgent.onEvent(context,eventType,msg);
    }
}
