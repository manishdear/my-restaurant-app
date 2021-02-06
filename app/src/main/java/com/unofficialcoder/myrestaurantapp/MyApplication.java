package com.unofficialcoder.myrestaurantapp;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.unofficialcoder.myrestaurantapp.Retrofit.IMyRestaurantAPI;
import com.unofficialcoder.myrestaurantapp.Retrofit.RetrofitClient;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.network.MyVolley;
import com.unofficialcoder.myrestaurantapp.storage.MySharedPreferences;

import net.gotev.uploadservice.UploadService;

public class MyApplication extends Application {

    public static MyApplication mInstance;
    public static RequestQueue mRequestQue;
    public static IMyRestaurantAPI myRestaurantAPI;
    public static MySharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = MySharedPreferences.getInstance(this);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        mRequestQue = MyVolley.getInstance().getRequestQueue();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }


}
