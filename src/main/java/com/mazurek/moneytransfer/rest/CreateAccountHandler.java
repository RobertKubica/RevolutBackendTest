package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.CreateAccountRequest;
import com.mazurek.moneytransfer.rest.responses.CreateAccountResponse;
import com.mazurek.moneytransfer.rest.responses.Response;

class CreateAccountHandler extends AbstractPostHandler<CreateAccountRequest> {
    CreateAccountHandler(MoneyTransferController controller) {
        super(controller);
    }


    @Override
    Response invokeOkAction(CreateAccountRequest request) {
        String accountId = moneyTransferController.createAccount(request.getOwner(), request.getPhoneNumber());
        CreateAccountResponse response = new CreateAccountResponse(accountId);
        return response;
    }

    @Override
    CreateAccountRequest parseRequest(String body) {
        return gson.fromJson(body, CreateAccountRequest.class);
    }

    @Override
    void validateRequest(CreateAccountRequest request) throws IllegalArgumentException {
        Preconditions.checkArgument(request.getOwner()!=null,"Owner cannot be null");
        Preconditions.checkArgument(request.getPhoneNumber()!=null,"Phone number cannot be null");
    }


}
