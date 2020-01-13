package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.DepositRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

public class DepositHandler implements HttpHandler {
    private final MoneyTransferController controller;

    public DepositHandler(MoneyTransferController controller) {
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
        DepositRequest depositRequest = gson.fromJson(body, DepositRequest.class);
        try {
            validateRequest(depositRequest);
            controller.deposit(depositRequest.getAccountId(), depositRequest.getAmount());
            BalanceResponse response = controller.getAccountViewById(depositRequest.getAccountId())
                    .map(v -> {
                        BalanceResponse balanceResponse = new BalanceResponse();
                        balanceResponse.setBalance(v.getBalance());
                        balanceResponse.setAccountId(depositRequest.getAccountId());
                        return balanceResponse;
                    }).orElseThrow(() -> new RuntimeException(String.format("Couldn't find account with id: %s", depositRequest.getAccountId())));
            httpServerExchange.setStatusCode(StatusCodes.OK);
            httpServerExchange.getResponseSender().send(gson.toJson(response));
        } catch (Exception ex) {
            httpServerExchange.setStatusCode(StatusCodes.BAD_REQUEST);
            httpServerExchange.getResponseSender().send(ex.getMessage());
        }
    }

    private void validateRequest(DepositRequest depositRequest) {
        Preconditions.checkNotNull(depositRequest.getAccountId(), "Account id cannot be null");
        Preconditions.checkNotNull(depositRequest.getAmount(), "Amount cannot be null");
    }
}
