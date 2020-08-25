package com.pushkar.packagecustomer.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequestData {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("deviceToken")
    private String deviceToken;

    public LoginRequestData(String email, String password, String deviceToken) {
        this.email = email;
        this.password = password;
        this.deviceToken = deviceToken;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
}
