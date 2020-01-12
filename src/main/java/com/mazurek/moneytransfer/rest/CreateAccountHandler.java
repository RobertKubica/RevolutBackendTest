package com.mazurek.moneytransfer.rest;

import com.google.gson.Gson;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.CreateAccountRequest;
import com.mazurek.moneytransfer.rest.responses.CreateAccountResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

class CreateAccountHandler implements HttpHandler {
    private final MoneyTransferController controller;

    CreateAccountHandler(MoneyTransferController controller) {
        this.controller = controller;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(this);
            return;
        }
        if (httpServerExchange.getRequestMethod().equalToString("POST")) {
            httpServerExchange.startBlocking();
            String body = IOUtils.toString(httpServerExchange.getInputStream(), Charset.defaultCharset());
            Gson gson = new Gson();
            CreateAccountRequest createAccountRequest = gson.fromJson(body, CreateAccountRequest.class);
            try {
                String accountId = controller.createAccount(createAccountRequest.getOwner(), createAccountRequest.getPhoneNumber());
                CreateAccountResponse response = new CreateAccountResponse(accountId);
                httpServerExchange.setStatusCode(StatusCodes.OK);
                httpServerExchange.getResponseSender().send(gson.toJson(response));
            } catch (Exception ex) {
                httpServerExchange.setStatusCode(StatusCodes.BAD_REQUEST);
                httpServerExchange.getResponseSender().send(ex.getMessage());
            }
        }
    }


}
