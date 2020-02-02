package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.AccountView;
import com.mazurek.moneytransfer.rest.requests.TransferRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import com.mazurek.moneytransfer.rest.responses.Response;

public class TransferHandler extends AbstractPostHandler<TransferRequest> {
    public TransferHandler(MoneyTransferController controller) {
        super(controller);
    }

    @Override
    Response invokeOkAction(TransferRequest transferRequest) {
        moneyTransferController.transfer(transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(), transferRequest.getAmount());

        AccountView accountView = moneyTransferController.getAccountViewById(transferRequest.getSourceAccountId());
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(accountView.getBalance());
        balanceResponse.setAccountId(transferRequest.getSourceAccountId());

        return balanceResponse;
    }

    @Override
    TransferRequest parseRequest(String body) {
        return gson.fromJson(body,TransferRequest.class);
    }

    @Override
    protected void validateRequest(TransferRequest transferRequest) {
        Preconditions.checkNotNull(transferRequest.getSourceAccountId(), "Source account cannot be null");
        Preconditions.checkNotNull(transferRequest.getTargetAccountId(), "Target account cannot be null");
        Preconditions.checkNotNull(transferRequest.getAmount(), "Amount cannot be null");
    }

}
