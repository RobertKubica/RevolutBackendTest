package com.mazurek.moneytransfer.model;

import java.math.BigDecimal;

public class Account implements AccountView {

    private final Person owner;

    private BigDecimal balance = BigDecimal.ZERO;

    public Account(Person owner) {
        this.owner = owner;
            }

    @Override
    public Person getOwner() {
        return owner;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }


}
