package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.AccountView;
import com.mazurek.moneytransfer.rest.requests.BalanceChangeRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import com.mazurek.moneytransfer.rest.responses.Response;

public abstract class BalanceChangeHandler extends AbstractPostHandler<BalanceChangeRequest> {
    public BalanceChangeHandler(MoneyTransferController controller) {
        super(controller);
    }


    @Override
    Response invokeOkAction(BalanceChangeRequest balanceChangeRequest) {
        invokeBalanceChange(balanceChangeRequest);

        AccountView accountView = moneyTransferController.getAccountViewById(balanceChangeRequest.getAccountId());
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(accountView.getBalance());
        balanceResponse.setAccountId(balanceChangeRequest.getAccountId());

        return balanceResponse;
    }

    protected void validateRequest(BalanceChangeRequest balanceChangeRequest) {
        Preconditions.checkNotNull(balanceChangeRequest.getAccountId(), "Account id cannot be null");
        Preconditions.checkNotNull(balanceChangeRequest.getAmount(), "Amount cannot be null");
    }

    @Override
    BalanceChangeRequest parseRequest(String body) {
        return gson.fromJson(body,BalanceChangeRequest.class);
    }

    protected abstract void invokeBalanceChange(BalanceChangeRequest balanceChangeRequest);
}
