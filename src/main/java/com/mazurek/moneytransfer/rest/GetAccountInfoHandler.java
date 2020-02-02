package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.rest.requests.GetAccountInfoRequest;
import com.mazurek.moneytransfer.rest.responses.AccountViewResponse;
import com.mazurek.moneytransfer.rest.responses.Response;

import java.util.Map;

class GetAccountInfoHandler extends AbstractGetHandler<GetAccountInfoRequest> {
    public GetAccountInfoHandler(MoneyTransferController controller) {
        super(controller);
    }

    @Override
    GetAccountInfoRequest createRequestFromParameters(Map<String, String> parameters) {
        return new GetAccountInfoRequest(parameters.get("id"));
    }


    @Override
    Response invokeOkAction(GetAccountInfoRequest request) {
        Account body = moneyTransferController.getAccount(request.id);
        return new AccountViewResponse(body.getBalance(), body.getOwner());
    }

    @Override
    void validateRequest(GetAccountInfoRequest request) throws IllegalArgumentException {
        Preconditions.checkArgument(request.getId() != null, "Id cannot be null");
    }
}
