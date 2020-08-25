package com.pushkar.packagecustomer.service;

import com.pushkar.packagecustomer.model.service.EncryptRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationService {

    @POST("/customer/register")
    Call<ResponseBody> registerUser(@Body EncryptRequest request);
}
