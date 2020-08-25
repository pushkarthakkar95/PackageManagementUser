package com.pushkar.packagecustomer.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pushkar.packagecustomer.model.service.EncryptRequest;
import com.pushkar.packagecustomer.model.service.ErrorResponse;
import com.pushkar.packagecustomer.model.service.IResponse;
import com.pushkar.packagecustomer.model.service.RegisteredUserDataHolder;
import com.pushkar.packagecustomer.service.EncryptionWrapper;
import com.pushkar.packagecustomer.service.RegistrationService;
import com.pushkar.packagecustomer.utils.Constants;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterUserRepository extends BaseRepository {
    private final String TAG = RegisterUserRepository.class.getSimpleName();
    private MutableLiveData<IResponse> registerUserResponseLiveData;
    private static RegisterUserRepository registerUserRepository;
    private RegistrationService service;
    private Gson gson = new Gson();
    public static RegisterUserRepository getRegisterUserRepository(){
        if(registerUserRepository == null){
            synchronized (RegisterUserRepository.class){
                if(registerUserRepository == null){
                    registerUserRepository = new RegisterUserRepository();
                }
            }
        }
        return registerUserRepository;
    }

    private RegisterUserRepository(){
        registerUserResponseLiveData = new MutableLiveData<>();
        service = getRegistrationService();
    }

    public void attemptRegistration(RegisteredUserDataHolder userDataHolder,EncryptRequest request){
        Log.d(TAG, "attemptRegistration - encrypt request string looks like: "+request.getEncryptString());
        service.registerUser(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse");
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse successful");
                    registerUserResponseLiveData.postValue(userDataHolder);
                }else{
                    Log.d(TAG, "onResponse not successful");
                    try {
                        String encryptedErrorResponseStr = response.errorBody().string();
                        Log.d(TAG, "onResponse not successful encrypted error response string: "+encryptedErrorResponseStr);
                        String decryptedErrorResponseStr = EncryptionWrapper.decrypt(encryptedErrorResponseStr);
                        Log.d(TAG, "onResponse not successful decrypted error response string: "+decryptedErrorResponseStr);
                        ErrorResponse errorResponse = gson.fromJson(decryptedErrorResponseStr,ErrorResponse.class);
                        registerUserResponseLiveData.postValue(errorResponse);
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse not successful Exception");
                        e.printStackTrace();
                        ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                                ,new ArrayList<>());
                        registerUserResponseLiveData.postValue(errorResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure");
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage()+t.getMessage());
                ErrorResponse errorResponse = new ErrorResponse("Something went wrong with the system. Please try again later"
                        ,new ArrayList<>());
                registerUserResponseLiveData.postValue(errorResponse);
            }
        });
    }

    public MutableLiveData<IResponse> getRegisterUserResponseLiveData() {
        return registerUserResponseLiveData;
    }
}
