package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.BalanceChangeRequest;

public class DepositHandler extends BalanceChangeHandler {

    public DepositHandler(MoneyTransferController controller) {
        super(controller);
    }

    @Override
    protected void invokeBalanceChange(BalanceChangeRequest balanceChangeRequest) {
        controller.deposit(balanceChangeRequest.getAccountId(), balanceChangeRequest.getAmount());
    }
}
