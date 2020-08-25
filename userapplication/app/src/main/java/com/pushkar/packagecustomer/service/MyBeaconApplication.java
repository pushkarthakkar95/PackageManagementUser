package com.pushkar.packagecustomer.service;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.estimote.coresdk.service.BeaconManager;
import com.pushkar.packagecustomer.utils.Constants;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyBeaconApplication extends Application {
    private BeaconManager beaconManager;
    private static Object object;
    private static String userEmail = "";

    @Override
    public void onCreate() {
        super.onCreate();
        if (beaconManager==null){
            synchronized (MyBeaconApplication.class){
                if(beaconManager==null){
                    Log.d(TAG, "onCreate: called");
                    beaconManager = new BeaconManager(getApplicationContext());
                    Log.d(TAG, "onCreate: called beacon manager is: "+beaconManager);
                    object = beaconManager;
                }
            }
        }
        userEmail = getApplicationContext().getSharedPreferences(Constants.PREFERENCES_KEY_USER,MODE_PRIVATE)
                .getString(Constants.PREFERENCES_EMAIL_KEY,"");
    }


    public static BeaconManager getBeaconManager() {
        Log.d(TAG, "getBeaconManager: "+object);
        return (BeaconManager) object;
    }

    public static String getUserEmail(){
        return userEmail;
    }

    public void setUserEmail(Context context){
        userEmail = context.getApplicationContext().getSharedPreferences(Constants.PREFERENCES_KEY_USER,MODE_PRIVATE)
                .getString(Constants.PREFERENCES_EMAIL_KEY,"");
    }

}
