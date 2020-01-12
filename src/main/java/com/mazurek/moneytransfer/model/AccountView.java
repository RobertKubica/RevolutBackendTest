package com.mazurek.moneytransfer.model;

import java.math.BigDecimal;

public interface AccountView {
    Person getOwner();

    BigDecimal getBalance();
}
