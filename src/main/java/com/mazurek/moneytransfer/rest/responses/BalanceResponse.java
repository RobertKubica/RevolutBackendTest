package com.mazurek.moneytransfer.rest.responses;

import java.math.BigDecimal;

public class BalanceResponse implements Response{
    private String accountId;
    private BigDecimal balance;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
