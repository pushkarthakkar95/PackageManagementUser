package com.pushkar.packagecustomer.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.pushkar.packagecustomer.model.service.EncryptRequest;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.ListOrdersRequest;
import com.pushkar.packagecustomer.model.service.LogoutRequestData;
import com.pushkar.packagecustomer.repository.HomeRepository;
import com.pushkar.packagecustomer.service.EncryptionWrapper;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<IResponse> logoutResponseMutableLiveData;
    private MutableLiveData<IResponse> getAllPackagesMutableLiveData;
    private HomeRepository homeRepository = HomeRepository.getHomeRepository();
    private Gson gson = new Gson();
    private final String TAG = HomeViewModel.class.getSimpleName();

    public HomeViewModel() {
        logoutResponseMutableLiveData = homeRepository.getLogoutResponseMutableLiveData();
        getAllPackagesMutableLiveData = homeRepository.getListOfOrdersResponseMutableLiveData();
    }

    public MutableLiveData<IResponse> getLogoutResponseMutableLiveData() {
        return logoutResponseMutableLiveData;
    }

    public MutableLiveData<IResponse> getGetAllPackagesMutableLiveData() {
        return getAllPackagesMutableLiveData;
    }

    public void attemptLogout(String email){
        LogoutRequestData logoutRequestData = new LogoutRequestData(email);
        String jsonStr = gson.toJson(logoutRequestData);
        Log.d(TAG, "attemptLogout with data: "+jsonStr);
        try {
            String encryptedString = EncryptionWrapper.encrypt(jsonStr);
            Log.d(TAG, "attemptLogout with encrypted string: "+encryptedString);
            EncryptRequest request =  new EncryptRequest(encryptedString);
            homeRepository.attemptLogout(request);
        } catch (Exception e) {
            Log.d(TAG, "attemptLogout exception: "+e.getMessage()+e.getLocalizedMessage());
            e.printStackTrace();
            sendGenericErrorResponse(logoutResponseMutableLiveData);
        }
    }

    public void getAllPackages(String email){
        ListOrdersRequest request = new ListOrdersRequest(email);
        String jsonStr = gson.toJson(request);
        Log.d(TAG, "getAllPackages request string: "+jsonStr);
        try {
            String encryptedStr =  EncryptionWrapper.encrypt(jsonStr);
            EncryptRequest encryptRequest =  new EncryptRequest(encryptedStr);
            homeRepository.attemptGetAllPackagesForUser(encryptRequest);
        } catch (Exception e) {
            Log.d(TAG, "getAllPackages exception: "+e.getMessage()+e.getLocalizedMessage());
            e.printStackTrace();
            sendGenericErrorResponse(getAllPackagesMutableLiveData);
        }
    }

    private void sendGenericErrorResponse(MutableLiveData<IResponse> mutableLiveData){
        ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                ,new ArrayList<>());
        mutableLiveData.postValue(errorResponse);
    }
}
