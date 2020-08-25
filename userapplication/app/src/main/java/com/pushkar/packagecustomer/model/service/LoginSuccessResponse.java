package com.pushkar.packagecustomer.model.service;

import com.google.gson.annotations.SerializedName;

public class LoginSuccessResponse implements IResponse {

    @SerializedName("name")
    private String nameOfUser;

    @SerializedName("deviceToken")
    private String deviceToken;


    public String getNameOfUser() {
        return nameOfUser;
    }

    public void setNameOfUser(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }
}
