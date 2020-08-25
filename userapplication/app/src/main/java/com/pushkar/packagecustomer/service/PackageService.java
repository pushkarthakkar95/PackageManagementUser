package com.pushkar.packagecustomer.service;

import com.pushkar.packagecustomer.model.service.EncryptRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface PackageService {
    @POST("/package/all/email")
    Call<ResponseBody> getListOfOrders(@Body EncryptRequest request);

    @PUT("/package/user/at/store")
    Call<ResponseBody> notifyUserAtStore(@Body EncryptRequest request);
}
