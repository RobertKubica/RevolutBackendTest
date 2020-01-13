package com.mazurek.moneytransfer.rest;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.AccountView;
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

            AccountView accountView = controller.getAccountViewById(balanceChangeRequest.getAccountId());
            BalanceResponse balanceResponse = new BalanceResponse();
            balanceResponse.setBalance(accountView.getBalance());
            balanceResponse.setAccountId(balanceChangeRequest.getAccountId());

            httpServerExchange.setStatusCode(StatusCodes.OK);
            httpServerExchange.getResponseSender().send(gson.toJson(balanceResponse));
        } catch (Exception ex) {
            httpServerExchange.setStatusCode(StatusCodes.BAD_REQUEST);
            String message = Strings.nullToEmpty(ex.getMessage());
            httpServerExchange.getResponseSender().send(message);
        }
    }

    private void validateRequest(BalanceChangeRequest balanceChangeRequest) {
        Preconditions.checkNotNull(balanceChangeRequest.getAccountId(), "Account id cannot be null");
        Preconditions.checkNotNull(balanceChangeRequest.getAmount(), "Amount cannot be null");
    }

    protected abstract void invokeBalanceChange(BalanceChangeRequest balanceChangeRequest);
}
