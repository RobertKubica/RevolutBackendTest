package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.mazurek.moneytransfer.MoneyTransferController;
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

            BalanceResponse response = controller.getAccountViewById(transferRequest.getSourceAccountId())
                    .map(v -> {
                        BalanceResponse balanceResponse = new BalanceResponse();
                        balanceResponse.setBalance(v.getBalance());
                        balanceResponse.setAccountId(transferRequest.getSourceAccountId());
                        return balanceResponse;
                    }).orElseThrow(() -> new RuntimeException(String.format("Couldn't find account with id: %s", transferRequest.getSourceAccountId())));

            httpServerExchange.setStatusCode(StatusCodes.OK);
            httpServerExchange.getResponseSender().send(gson.toJson(response));
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
