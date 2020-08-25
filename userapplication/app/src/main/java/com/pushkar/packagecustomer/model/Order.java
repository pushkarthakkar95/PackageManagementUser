package com.pushkar.packagecustomer.model;

import com.google.gson.annotations.SerializedName;
import com.pushkar.packagecustomer.model.service.Status;

public class Order {
    private String trackingNumber;
    private String description;

    @SerializedName("status")
    private Status packageStatus;

    @SerializedName("storeId")
    private String storeNum;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getDescription() {
        return description;
    }

    public Status getPackageStatus() {
        return packageStatus;
    }

    public String getStoreNum() {
        return storeNum;
    }
}
