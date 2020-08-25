package com.pushkar.packagecustomer.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pushkar.packagecustomer.model.Order;
import com.pushkar.packagecustomer.model.service.EncryptRequest;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.ListOfOrderSuccessResponse;
import com.pushkar.packagecustomer.model.service.LogoutSuccessResponse;
import com.pushkar.packagecustomer.model.service.UserAtStoreRequest;
import com.pushkar.packagecustomer.service.EncryptionWrapper;
import com.pushkar.packagecustomer.service.LoginService;
import com.pushkar.packagecustomer.service.PackageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository extends BaseRepository {
    private final String TAG = HomeRepository.class.getSimpleName();
    private static HomeRepository homeRepository;
    private MutableLiveData<IResponse> logoutResponseMutableLiveData;
    private MutableLiveData<IResponse> listOfOrdersResponseMutableLiveData;
    private LoginService loginService;
    private PackageService packageService;
    private Gson gson = new Gson();
    public static HomeRepository getHomeRepository(){
        if(homeRepository == null){
            synchronized (HomeRepository.class){
                if(homeRepository == null){
                    homeRepository = new HomeRepository();
                }
            }
        }
        return homeRepository;
    }

    private HomeRepository(){
        logoutResponseMutableLiveData = new MutableLiveData<>();
        listOfOrdersResponseMutableLiveData = new MutableLiveData<>();
        loginService = getLoginService();
        packageService = getPackageService();
    }

    public void notifyAdminUserAtStore(UserAtStoreRequest userAtStoreRequest){
        String jsonStr = gson.toJson(userAtStoreRequest);
        Log.d(TAG, "notifyAdminUserAtStore request: "+jsonStr);
        try {
            String encryptedStr = EncryptionWrapper.encrypt(jsonStr);
            packageService.notifyUserAtStore(new EncryptRequest(encryptedStr)).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //do nothing
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //do nothing
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "notifyAdminUserAtStore exception");
        }
    }

    public void attemptLogout(EncryptRequest request){
        loginService.logout(request).enqueue(new callbackHandler(getLogoutResponseMutableLiveData()) {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200){
                    logoutResponseMutableLiveData.postValue(new LogoutSuccessResponse());
                }else{
                    Log.d(TAG, "onResponse failure");
                    try {
                        String encryptedStr = response.errorBody().string();
                        Log.d(TAG, "onResponse logout attempt encryptedResponse string: "+encryptedStr);
                        String decryptedStr = EncryptionWrapper.decrypt(encryptedStr);
                        Log.d(TAG, "onResponse logout attempt decrypted string: "+decryptedStr);
                        ErrorResponse errorResponse = gson.fromJson(decryptedStr,ErrorResponse.class);
                        logoutResponseMutableLiveData.postValue(errorResponse);
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse failure exception"+e.getLocalizedMessage()+e.getMessage());
                        e.printStackTrace();
                        sendGenericErrorResponse(getLogoutResponseMutableLiveData());
                    }
                }
            }

        });
    }

    public void attemptGetAllPackagesForUser(EncryptRequest request){
        packageService.getListOfOrders(request).enqueue(new callbackHandler(getListOfOrdersResponseMutableLiveData()){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse attemptGetAllPackagesForUser");
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse attemptGetAllPackagesForUser is successful");
                    try {
                        String rawEncryptedResponseStr = response.body().string();
                        Log.d(TAG, "onResponse attemptGetAllPackagesForUser encrypted response string: "+rawEncryptedResponseStr);
                        String decryptedJsonStr = EncryptionWrapper.decrypt(rawEncryptedResponseStr);
                        Log.d(TAG, "onResponse attemptGetAllPackagesForUser decrypted string: "+decryptedJsonStr);
                        ListOfOrderSuccessResponse listOfOrderSuccessResponse = gson.fromJson(decryptedJsonStr,
                                ListOfOrderSuccessResponse.class);
                        listOfOrdersResponseMutableLiveData.postValue(listOfOrderSuccessResponse);
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse success exception"+e.getLocalizedMessage()+e.getMessage());
                        e.printStackTrace();
                        sendGenericErrorResponse(getListOfOrdersResponseMutableLiveData());
                    }
                } else{
                    try {
                        String encryptedErrorResponse = response.errorBody().string();
                        String decryptedString = EncryptionWrapper.decrypt(encryptedErrorResponse);
                        Log.d(TAG, "onResponse failure decrypted string: "+decryptedString);
                        ErrorResponse errorResponse =  gson.fromJson(decryptedString,ErrorResponse.class);
                        listOfOrdersResponseMutableLiveData.postValue(errorResponse);
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse failure exception"+e.getLocalizedMessage()+e.getMessage());
                        e.printStackTrace();
                        sendGenericErrorResponse(getListOfOrdersResponseMutableLiveData());
                    }

                }
            }
        });
    }



    private void sendGenericErrorResponse(MutableLiveData<IResponse> mutableLiveData){
        ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                ,new ArrayList<>());
        mutableLiveData.postValue(errorResponse);
    }


    public MutableLiveData<IResponse> getLogoutResponseMutableLiveData() {
        return logoutResponseMutableLiveData;
    }

    public MutableLiveData<IResponse> getListOfOrdersResponseMutableLiveData() {
        return listOfOrdersResponseMutableLiveData;
    }


    private class callbackHandler implements Callback<ResponseBody>{
        private MutableLiveData<IResponse> mutableLiveData;

        public callbackHandler(MutableLiveData<IResponse> mutableLiveData) {
            this.mutableLiveData = mutableLiveData;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            //overridden
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.d(TAG, "onFailure: "+t.getMessage()+t.getLocalizedMessage());
            sendGenericErrorResponse(mutableLiveData);
        }
    }
}
