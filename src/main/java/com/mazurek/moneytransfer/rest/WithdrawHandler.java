package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.BalanceChangeRequest;

public class WithdrawHandler extends BalanceChangeHandler {
    public WithdrawHandler(MoneyTransferController controller) {
        super(controller);
    }

    @Override
    protected void invokeBalanceChange(BalanceChangeRequest balanceChangeRequest) {
        controller.withdraw(balanceChangeRequest.getAccountId(), balanceChangeRequest.getAmount());
    }
}
