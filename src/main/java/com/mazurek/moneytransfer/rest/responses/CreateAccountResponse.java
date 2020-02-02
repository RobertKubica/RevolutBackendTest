package com.mazurek.moneytransfer.rest.responses;

public class CreateAccountResponse implements Response {
    private final String accountId;

    public CreateAccountResponse(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }
}
