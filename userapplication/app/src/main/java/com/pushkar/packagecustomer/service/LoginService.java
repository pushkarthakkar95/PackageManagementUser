package com.pushkar.packagecustomer.service;

import com.pushkar.packagecustomer.model.service.EncryptRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/customer/login")
    Call<ResponseBody> login(@Body EncryptRequest request);

    @POST("/customer/logout")
    Call<ResponseBody> logout(@Body EncryptRequest request);

}
