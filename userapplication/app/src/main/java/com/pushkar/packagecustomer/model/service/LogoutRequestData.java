package com.pushkar.packagecustomer.model.service;

import com.google.gson.annotations.SerializedName;

public class LogoutRequestData {
    @SerializedName("email")
    private String email;

    public LogoutRequestData(String email) {
        this.email = email;
    }
}
