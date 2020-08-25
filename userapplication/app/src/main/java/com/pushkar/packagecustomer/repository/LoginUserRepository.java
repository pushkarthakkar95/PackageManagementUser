package com.pushkar.packagecustomer.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pushkar.packagecustomer.model.service.EncryptRequest;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.LoginSuccessResponse;
import com.pushkar.packagecustomer.service.EncryptionWrapper;
import com.pushkar.packagecustomer.service.LoginService;
import com.pushkar.packagecustomer.utils.Constants;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginUserRepository extends BaseRepository {
    private static LoginUserRepository loginUserRepository;
    private static MutableLiveData<IResponse> loginResponseLiveData;
    private LoginService loginService;
    private static final String TAG = LoginUserRepository.class.getSimpleName();
    private Gson gson = new Gson();

    public static LoginUserRepository getLoginUserRepository(){
        if(loginUserRepository == null){
            synchronized (LoginUserRepository.class){
                if(loginUserRepository == null){
                    loginUserRepository = new LoginUserRepository();
                }
            }
        }
        return loginUserRepository;
    }

    private LoginUserRepository(){
        loginResponseLiveData = new MutableLiveData<>();
        loginService = getLoginService();
    }

    public void attemptLogin(EncryptRequest encryptLoginRequest){
        Log.d(TAG, "attemptLogin - with encrypted login string as : "+encryptLoginRequest.getEncryptString());
        loginService.login(encryptLoginRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse");
                if(response.isSuccessful()){
                    try {
                        String encryptedResponseStr = response.body().string();
                        Log.d(TAG, "onResponse - Successful with raw encrypted string: "+encryptedResponseStr);
                        String decryptedResponseStr = EncryptionWrapper.decrypt(encryptedResponseStr);
                        Log.d(TAG, "onResponse - Successful with raw decrypted string: "+decryptedResponseStr);
                        LoginSuccessResponse loginSuccessResponse = gson.fromJson(decryptedResponseStr,
                                LoginSuccessResponse.class);
                        loginResponseLiveData.postValue(loginSuccessResponse);
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse - Exception ");
                        e.printStackTrace();
                        ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                                ,new ArrayList<>());
                        loginResponseLiveData.postValue(errorResponse);
                    }
                }else{
                    Log.d(TAG, "onResponse is not successful");
                    try {
                        String encryptedErrorResponseStr = response.errorBody().string();
                        Log.d(TAG, "onResponse - error response raw encrypted string is: "+encryptedErrorResponseStr);
                        String decryptedErrorResponseStr = EncryptionWrapper.decrypt(encryptedErrorResponseStr);
                        Log.d(TAG, "onResponse - error response raw decrypted string is: "+decryptedErrorResponseStr);
                        ErrorResponse errorResponse = gson.fromJson(decryptedErrorResponseStr
                                ,ErrorResponse.class);
                        loginResponseLiveData.postValue(errorResponse);
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse - is not successful Exception");
                        e.printStackTrace();
                        ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                                ,new ArrayList<>());
                        loginResponseLiveData.postValue(errorResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure");
                ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                        ,new ArrayList<>());
                loginResponseLiveData.postValue(errorResponse);
            }
        });
    }

    public MutableLiveData<IResponse> getLoginResponseLiveData(){
        return loginResponseLiveData;
    }
}
