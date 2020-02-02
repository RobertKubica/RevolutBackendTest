package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.rest.requests.TransferRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;

public class TransferHandler extends AbstractPostHandler<TransferRequest> {
    public TransferHandler(MoneyTransferController controller) {
        super(controller);
    }

    @Override
    BalanceResponse invokeOkAction(TransferRequest request) {
        moneyTransferController.transfer(request.getSourceAccountId(), request.getTargetAccountId(), request.getAmount());

        Account accountView = moneyTransferController.getAccount(request.getSourceAccountId());
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(accountView.getBalance());
        balanceResponse.setAccountId(request.getSourceAccountId());

        return balanceResponse;
    }

    @Override
    TransferRequest parseRequest(String body) {
        return gson.fromJson(body, TransferRequest.class);
    }

    @Override
    protected void validateRequest(TransferRequest transferRequest) {
        Preconditions.checkArgument(transferRequest.getSourceAccountId() != null, "Source account cannot be null");
        Preconditions.checkArgument(transferRequest.getTargetAccountId() != null, "Target account cannot be null");
        Preconditions.checkArgument(transferRequest.getAmount() != null, "Amount cannot be null");
    }

}
