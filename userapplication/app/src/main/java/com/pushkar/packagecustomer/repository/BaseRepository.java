package com.pushkar.packagecustomer.repository;

import com.pushkar.packagecustomer.service.LoginService;
import com.pushkar.packagecustomer.service.PackageService;
import com.pushkar.packagecustomer.service.RegistrationService;
import com.pushkar.packagecustomer.utils.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseRepository{

    protected LoginService getLoginService(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        LoginService result = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LoginService.class);
        return result;
    }

    protected RegistrationService getRegistrationService(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        RegistrationService result = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RegistrationService.class);
        return result;
    }

    protected PackageService getPackageService(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        PackageService result = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PackageService.class);
        return result;
    }
}
