package com.mazurek.moneytransfer.rest.requests;

import java.math.BigDecimal;

public class TransferRequest implements Request{
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
