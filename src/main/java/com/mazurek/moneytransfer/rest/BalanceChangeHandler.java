package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.rest.requests.BalanceChangeRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import com.mazurek.moneytransfer.rest.responses.Response;

public abstract class BalanceChangeHandler extends AbstractPostHandler<BalanceChangeRequest> {
    public BalanceChangeHandler(MoneyTransferController controller) {
        super(controller);
    }


    @Override
    BalanceResponse invokeOkAction(BalanceChangeRequest request) {
        invokeBalanceChange(request);

        Account accountView = moneyTransferController.getAccount(request.getAccountId());
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(accountView.getBalance());
        balanceResponse.setAccountId(request.getAccountId());

        return balanceResponse;
    }

    protected void validateRequest(BalanceChangeRequest balanceChangeRequest) {
        Preconditions.checkArgument(balanceChangeRequest.getAccountId() != null, "Account id cannot be null");
        Preconditions.checkArgument(balanceChangeRequest.getAmount() != null, "Amount cannot be null");
    }

    @Override
    BalanceChangeRequest parseRequest(String body) {
        return gson.fromJson(body, BalanceChangeRequest.class);
    }

    protected abstract void invokeBalanceChange(BalanceChangeRequest balanceChangeRequest);
}
