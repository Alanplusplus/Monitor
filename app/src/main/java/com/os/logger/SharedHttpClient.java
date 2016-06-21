package com.os.logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Alan on 16/5/12.
 */
public class SharedHttpClient {
    private static OkHttpClient sClient;

    public static OkHttpClient getClient(){
        if (sClient == null){
            sClient = new OkHttpClient.Builder().readTimeout(360, TimeUnit.SECONDS).build();
        }
        return sClient;
    }
}
