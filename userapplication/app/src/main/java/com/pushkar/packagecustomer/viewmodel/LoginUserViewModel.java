package com.pushkar.packagecustomer.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.pushkar.packagecustomer.model.LoginRequestData;
import com.pushkar.packagecustomer.model.service.EncryptRequest;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.repository.LoginUserRepository;
import com.pushkar.packagecustomer.service.EncryptionWrapper;

import java.util.ArrayList;

public class LoginUserViewModel extends ViewModel {

    private LoginUserRepository loginUserRepository = LoginUserRepository.getLoginUserRepository();
    private MutableLiveData<IResponse> loginResponseLiveData;
    private static final String TAG = LoginUserViewModel.class.getSimpleName();
    private Gson gson = new Gson();

    public LoginUserViewModel() {
        loginResponseLiveData = loginUserRepository.getLoginResponseLiveData();
    }

    public void attemptLogin(LoginRequestData loginRequestData){
        Log.d(TAG, "attemptLogin - with login email: "+loginRequestData.getEmail()+" and password: "
                +loginRequestData.getPassword());
        String jsonStr = gson.toJson(loginRequestData);
        Log.d(TAG, "attemptLogin - with json string: "+jsonStr);
        try {
            String encryptedRequestStr = EncryptionWrapper.encrypt(jsonStr);
            Log.d(TAG, "attemptLogin - with encrypted string: "+encryptedRequestStr);
            EncryptRequest encryptRequest = new EncryptRequest(encryptedRequestStr);
            loginUserRepository.attemptLogin(encryptRequest);
        } catch (Exception e) {
            Log.d(TAG, "attemptLogin - Exception");
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                    ,new ArrayList<>());
            loginResponseLiveData.postValue(errorResponse);
        }
    }

    public MutableLiveData<IResponse> getLoginResponseLiveData() {
        return loginResponseLiveData;
    }
}
