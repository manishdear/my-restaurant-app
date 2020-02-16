package com.unofficialcoder.myrestaurantapp;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    //instance field
    private static SharedPreferences mSharedPreference;
    private static MySharedPreferences mInstance = null;
    private static Context mContext;

    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String ID_KEY  = "id";
    public static final String NAME_KEY  = "name";
    public static final String EMAIL_KEY  = "email";
    public static final String COUNTRY_CODE_KEY  = "ccp";
    public static final String MOBILE_KEY  = "mobile";
    public static final String GENDER_KEY  = "gender";
    public static final String WALLET_KEY  = "wallet";
    public static final String DOB_KEY  = "dob";
    public static final String COUNTRY_KEY  = "country";
    public static final String ADDRESS_KEY  = "address";
    public static final String PROFILE_PIC_URL  = "profile_pic";
    public static final String API_KEY  = "apiToken";

    //Shared Preference key
    private String KEY_PREFERENCE_NAME = "wapicash";
    private String KEY_DEFAULT_TAB = "";

    //private keyS
    public String KEY_DEFAULT = null;

    SharedPreferences.Editor editor;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "wapicash-sp";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    public MySharedPreferences() {
        mSharedPreference = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreference.edit();
    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    //Method to set boolean for (AppIntro)
    public void setBooleanKey(String keyname) {
        mSharedPreference.edit().putBoolean(keyname, true).apply();
    }

    public void setBooleanKey(String keyname, boolean state) {
        mSharedPreference.edit().putBoolean(keyname, state).apply();
    }

    /*
     * Method to get boolan key
     * true = means set
     * false = not set (show app intro)
     * */
    public boolean getBooleanKey(String keyname) {
        return mSharedPreference.getBoolean(keyname, false);
    }


    //Method to store user Mobile number
    public boolean setKey(String keyname, String mobile) {
        mSharedPreference.edit().putString(keyname, mobile).apply();
        return false;
    }

    //Method to get User mobile number
    public String getKey(String keyname) {
        return mSharedPreference.getString(keyname, KEY_DEFAULT);
    }

    public boolean setID(String keyname, String id) {
        mSharedPreference.edit().putString(keyname, id).apply();
        return false;
    }

    //Method to get User mobile number
    public String getID(String keyname) {
        return mSharedPreference.getString(keyname, KEY_DEFAULT);
    }

    public void setInt(String key, int value) {
        mSharedPreference.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return mSharedPreference.getInt(key, 0);
    }

    public Boolean chk(String key) {
        return mSharedPreference.contains(key);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.remove(key);
        editor.apply();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return mSharedPreference.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

}
