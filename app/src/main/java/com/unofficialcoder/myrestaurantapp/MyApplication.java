package com.unofficialcoder.myrestaurantapp;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.unofficialcoder.myrestaurantapp.network.MyVolley;

import net.gotev.uploadservice.UploadService;

public class MyApplication extends Application {

    public static MyApplication mInstance;
    public static RequestQueue mRequestQue;
    public static MySharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = MySharedPreferences.getInstance(this);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        mRequestQue = MyVolley.getInstance().getRequestQueue();
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }


}
