package com.mazurek.moneytransfer.rest.requests;

public class CreateAccountRequest implements Request {
    private String owner;
    private String phoneNumber;

    public String getOwner() {
        return owner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
