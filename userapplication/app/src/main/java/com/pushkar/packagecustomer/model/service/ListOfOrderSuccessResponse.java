package com.pushkar.packagecustomer.model.service;

import com.google.gson.annotations.SerializedName;
import com.pushkar.packagecustomer.model.Order;

import java.util.List;

public class ListOfOrderSuccessResponse implements IResponse{
    @SerializedName("lists")
    private List<Order> packageOrderList;

    public ListOfOrderSuccessResponse(List<Order> packageOrderList) {
        this.packageOrderList = packageOrderList;
    }

    public List<Order> getPackageOrderList() {
        return packageOrderList;
    }
}
