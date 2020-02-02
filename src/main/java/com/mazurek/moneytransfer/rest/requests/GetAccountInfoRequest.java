package com.mazurek.moneytransfer.rest.requests;

public class GetAccountInfoRequest implements Request {
    private final String id;

    public GetAccountInfoRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
