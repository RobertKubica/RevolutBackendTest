package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.AccountView;
import com.mazurek.moneytransfer.rest.requests.TransferRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

public class TransferHandler implements HttpHandler {
    private final MoneyTransferController controller;

    public TransferHandler(MoneyTransferController controller) {
        this.controller = controller;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(this);
            return;
        }
        httpServerExchange.startBlocking();
        String body = IOUtils.toString(httpServerExchange.getInputStream(), Charset.defaultCharset());
        Gson gson = new Gson();
        TransferRequest transferRequest = gson.fromJson(body, TransferRequest.class);

        try {
            validateRequest(transferRequest);

            controller.transfer(transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(), transferRequest.getAmount());

            AccountView accountView = controller.getAccountViewById(transferRequest.getSourceAccountId());
            BalanceResponse balanceResponse = new BalanceResponse();
            balanceResponse.setBalance(accountView.getBalance());
            balanceResponse.setAccountId(transferRequest.getSourceAccountId());
            httpServerExchange.setStatusCode(StatusCodes.OK);
            httpServerExchange.getResponseSender().send(gson.toJson(balanceResponse));
        } catch (Exception ex) {
            httpServerExchange.setStatusCode(StatusCodes.BAD_REQUEST);
            httpServerExchange.getResponseSender().send(ex.getMessage());
        }
    }

    private void validateRequest(TransferRequest transferRequest) {
        Preconditions.checkNotNull(transferRequest.getSourceAccountId(), "Source account cannot be null");
        Preconditions.checkNotNull(transferRequest.getTargetAccountId(), "Target account cannot be null");
        Preconditions.checkNotNull(transferRequest.getAmount(), "Amount cannot be null");
    }

}
