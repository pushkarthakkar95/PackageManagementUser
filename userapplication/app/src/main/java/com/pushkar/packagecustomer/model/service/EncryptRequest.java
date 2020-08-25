package com.pushkar.packagecustomer.model.service;

import com.google.gson.annotations.SerializedName;

public class EncryptRequest {
    @SerializedName("decryptString")
    private String encryptString;

    public EncryptRequest(String encryptString) {
        this.encryptString = encryptString;
    }

    public String getEncryptString() {
        return encryptString;
    }
}
