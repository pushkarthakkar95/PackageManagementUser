package com.pushkar.packagecustomer.model.service;

public class RegisterUserRequest {

    private String name;

    private String email;

    private String password;

    public RegisterUserRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
