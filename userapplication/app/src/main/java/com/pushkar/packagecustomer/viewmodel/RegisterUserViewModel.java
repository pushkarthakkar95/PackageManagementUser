package com.pushkar.packagecustomer.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.pushkar.packagecustomer.model.RegistrationData;
import com.pushkar.packagecustomer.model.service.EncryptRequest;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.RegisterUserRequest;
import com.pushkar.packagecustomer.model.service.RegisteredUserDataHolder;
import com.pushkar.packagecustomer.repository.RegisterUserRepository;
import com.pushkar.packagecustomer.service.EncryptionWrapper;

import java.util.ArrayList;

public class RegisterUserViewModel extends ViewModel {
    private RegisterUserRepository registerUserRepository = RegisterUserRepository.getRegisterUserRepository();
    private MutableLiveData<IResponse> registerUserResponseLiveData;
    private static final String TAG = RegisterUserViewModel.class.getSimpleName();
    private Gson gson = new Gson();

    public RegisterUserViewModel() {
        registerUserResponseLiveData = registerUserRepository.getRegisterUserResponseLiveData();
    }

    public void attemptRegistration(RegistrationData data){
        RegisterUserRequest request = new RegisterUserRequest(data.getName(),data.getEmail(),data.getPassword());
        Log.d(TAG, "attemptRegistration in viewmodel with request details - Name: "+data.getName()+" Email: "+
                data.getEmail()+" password: "+data.getPassword());
        String jsonStr = gson.toJson(request);
        try {
            EncryptRequest encryptRequest = new EncryptRequest(EncryptionWrapper.encrypt(jsonStr));
            registerUserRepository.attemptRegistration(new RegisteredUserDataHolder(data.getName(),data.getEmail()),encryptRequest);
        } catch (Exception e) {
            Log.d(TAG, "attemptRegistration - Exception");
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                    ,new ArrayList<>());
            registerUserResponseLiveData.postValue(errorResponse);
        }
    }

    public MutableLiveData<IResponse> getRegisterUserResponseLiveData() {
        return registerUserResponseLiveData;
    }
}
