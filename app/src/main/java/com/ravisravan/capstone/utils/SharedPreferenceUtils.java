package com.ravisravan.capstone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ravisravankumar on 15/10/16.
 */
public class SharedPreferenceUtils {

    Context mContext;
    private static SharedPreferenceUtils sInstance;
    private static Object sObject = new Object();
    private final SharedPreferences sharedPreferences;

    private SharedPreferenceUtils(Context context) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
    }

    public static SharedPreferenceUtils getInstance(Context context) {

        if (sInstance == null) {
            synchronized (sObject) {
                if(sInstance == null){
                    sInstance = new SharedPreferenceUtils(context);
                }
            }
        }
        return sInstance;
    }

    public void setStringPreference(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        //apply() is asynchronous use it when on Ui thread
        //commit() is synchronous
        editor.apply();
    }

    public String getStringPreference(String key,String defaultVal){
        return sharedPreferences.getString(key,defaultVal);
    }

    public void setBooleanPreference(String key,boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public boolean getBooleanPreference(String key,boolean defaultVal){
        return sharedPreferences.getBoolean(key,defaultVal);
    }
}
