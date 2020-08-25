package com.pushkar.packagecustomer.model.service;

import java.util.List;

public class ErrorResponse implements IResponse {
    private String message;

    private List<String> details;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    public ErrorResponse() {
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ErrorResponse { Message: "+message+" , Details: ");
        details.forEach( str ->{
            builder.append(str+", ");
        });
        return builder.toString();
    }
}
