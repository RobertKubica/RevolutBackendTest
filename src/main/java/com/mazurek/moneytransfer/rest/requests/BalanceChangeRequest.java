package com.mazurek.moneytransfer.rest.requests;

import java.math.BigDecimal;

public class BalanceChangeRequest implements Request {
    private String accountId;
    private BigDecimal amount;

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }


}
