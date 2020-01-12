package com.mazurek.moneytransfer.rest.requests;

public class CreateAccountRequest {
    private String owner;
    private String phoneNumber;

    public String getOwner() {
        return owner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
