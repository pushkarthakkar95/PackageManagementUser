package com.pushkar.packagecustomer.model;

public class RegistrationData {
    private String email,password,confirmPassword,name;

    public RegistrationData(String email, String password, String confirmPassword, String name) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getName() {
        return name;
    }
}
