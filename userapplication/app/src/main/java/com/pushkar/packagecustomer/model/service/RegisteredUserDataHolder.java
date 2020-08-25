package com.pushkar.packagecustomer.model.service;

public class RegisteredUserDataHolder implements IResponse{

    private String name;

    private String email;

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }

    public RegisteredUserDataHolder(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
