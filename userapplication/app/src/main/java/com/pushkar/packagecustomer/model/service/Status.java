package com.pushkar.packagecustomer.model.service;

public enum Status {
    IN_TRANSIT("IN TRANSIT"),
    DELIVERED("DELIVERED"),
    IN_STORE("IN STORE");

    private final String name;
    private Status(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
