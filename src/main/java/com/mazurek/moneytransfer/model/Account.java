package com.mazurek.moneytransfer.model;

import java.math.BigDecimal;

public class Account {

    private final Person owner;

    private BigDecimal balance;

    public Account(Person owner) {
        this.owner = owner;
            }

    public Person getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
