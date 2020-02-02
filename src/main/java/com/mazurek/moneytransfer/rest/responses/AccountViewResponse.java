package com.mazurek.moneytransfer.rest.responses;

import com.mazurek.moneytransfer.model.Person;

import java.math.BigDecimal;

public class AccountViewResponse implements Response{
    private final BigDecimal balance;
    private final Person owner;

    public AccountViewResponse(BigDecimal balance, Person owner) {
        this.balance = balance;
        this.owner = owner;
    }

    public Person getOwner() {
        return owner;
    }

    public BigDecimal getBalance(){
        return balance;
    }
}
