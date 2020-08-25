package com.pushkar.packagecustomer.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pushkar.packagecustomer.R;
import com.pushkar.packagecustomer.utils.Constants;
import com.pushkar.packagecustomer.view.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private final String TAG = SplashActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPreferences = getSharedPreferences(Constants.PREFERENCES_KEY_USER,MODE_PRIVATE);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashActivity.this
                , new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        updateTokenToPrefs(token);
                        Log.d(TAG, "onSuccess: "+token);
                    }
                });
        splash();
    }

    private void updateTokenToPrefs(String newToken){
        if(!newToken.equals(mPreferences.getString(Constants.PREFERENCES_DEVICE_TOKEN_KEY,""))){
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            preferencesEditor.putString(Constants.PREFERENCES_DEVICE_TOKEN_KEY,newToken);
            preferencesEditor.apply();
        }
    }

    private boolean isUserLoggedIn(){
        return mPreferences.getBoolean(Constants.PREFERENCES_IS_LOGGED_IN_KEY,false);
    }

    private void goToHomeScreen(){
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLoginScreen(boolean takeToUserInfo){
        Intent intent = new Intent(SplashActivity.this,LoginUserActivity.class);
        if(takeToUserInfo){
            //add put extra or serializable with this intent to take user to info page right after login
        }
        startActivity(intent);
        finish();
    }

    private void splash(){
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 2 seconds
                    sleep(2*1000);

                    // After 2 seconds redirect to another intent
                    goToNextScreen();

                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();
    }

    private void goToNextScreen(){
        if(isUserLoggedIn()){
            goToHomeScreen();
        }else{
            goToLoginScreen(false);
        }
    }
}
