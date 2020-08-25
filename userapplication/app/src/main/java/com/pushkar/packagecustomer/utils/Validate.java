package com.pushkar.packagecustomer.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.estimote.coresdk.recognition.packets.Beacon;
import com.pushkar.packagecustomer.model.LoginRequestData;
import com.pushkar.packagecustomer.model.RegistrationData;

public class Validate {
    public static boolean isLoginDataValid(LoginRequestData loginData){
        if(loginData.getEmail().isEmpty() || loginData.getPassword().isEmpty())
            return false;
        if(loginData.getPassword().length() < 5)
            return false;
        return isValidEmail(loginData.getEmail());
    }

    public static boolean isRegistrationDataValid(RegistrationData data){
        if(data.getEmail().isEmpty() || data.getPassword().length() < 5 || data.getConfirmPassword().length() < 5
                || data.getName().isEmpty()){
            return false;
        }
        if(!data.getPassword().equals(data.getConfirmPassword())){
            return false;
        }
        return isValidEmail(data.getEmail());
    }

    private static boolean isValidEmail(String email){
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    public static double getDistanceToBeacon(Beacon beacon){
        double raiseTo = (beacon.getMeasuredPower()-beacon.getRssi())/(10*2);
        double result =  Math.pow(10,raiseTo);
        return result;
    }
}
