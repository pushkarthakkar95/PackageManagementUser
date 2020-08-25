package com.pushkar.packagecustomer.model.service;

import com.pushkar.packagecustomer.model.Beacon;

public class UserAtStoreRequest {

    public String userEmail;

    public Beacon beacon;

    public UserAtStoreRequest(String userEmail, Beacon beacon) {
        this.userEmail = userEmail;
        this.beacon = beacon;
    }
}
