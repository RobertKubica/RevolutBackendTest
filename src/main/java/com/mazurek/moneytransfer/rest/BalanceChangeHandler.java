package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.BalanceChangeRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

public abstract class BalanceChangeHandler implements HttpHandler {
    protected final MoneyTransferController controller;

    public BalanceChangeHandler(MoneyTransferController controller) {
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
        BalanceChangeRequest balanceChangeRequest = gson.fromJson(body, BalanceChangeRequest.class);
        try {
            validateRequest(balanceChangeRequest);

            invokeBalanceChange(balanceChangeRequest);

            BalanceResponse response = controller.getAccountViewById(balanceChangeRequest.getAccountId())
                    .map(v -> {
                        BalanceResponse balanceResponse = new BalanceResponse();
                        balanceResponse.setBalance(v.getBalance());
                        balanceResponse.setAccountId(balanceChangeRequest.getAccountId());
                        return balanceResponse;
                    }).orElseThrow(() -> new RuntimeException(String.format("Couldn't find account with id: %s", balanceChangeRequest.getAccountId())));

            httpServerExchange.setStatusCode(StatusCodes.OK);
            httpServerExchange.getResponseSender().send(gson.toJson(response));
        } catch (Exception ex) {
            httpServerExchange.setStatusCode(StatusCodes.BAD_REQUEST);
            httpServerExchange.getResponseSender().send(ex.getMessage());
        }
    }

    private void validateRequest(BalanceChangeRequest balanceChangeRequest) {
        Preconditions.checkNotNull(balanceChangeRequest.getAccountId(), "Account id cannot be null");
        Preconditions.checkNotNull(balanceChangeRequest.getAmount(), "Amount cannot be null");
    }

    protected abstract void invokeBalanceChange(BalanceChangeRequest balanceChangeRequest);
}
