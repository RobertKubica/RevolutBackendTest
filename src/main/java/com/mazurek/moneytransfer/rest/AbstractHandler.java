package com.mazurek.moneytransfer.rest;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.exceptions.ResourceNotFoundException;
import com.mazurek.moneytransfer.rest.requests.Request;
import com.mazurek.moneytransfer.rest.responses.Response;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.io.IOException;

public abstract class AbstractHandler<T extends Request> implements HttpHandler {
    protected final Gson gson = new Gson();
    protected final MoneyTransferController moneyTransferController;

    public AbstractHandler(MoneyTransferController moneyTransferController) {
        this.moneyTransferController = moneyTransferController;
    }

    @Override
    public final void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if (httpServerExchange.isInIoThread()) {
            httpServerExchange.dispatch(this);
            return;
        }
        httpServerExchange.startBlocking();
        T createAccountRequest = getData(httpServerExchange);
        try {
            validateRequest(createAccountRequest);
            Response response = invokeOkAction(createAccountRequest);
            httpServerExchange.setStatusCode(StatusCodes.OK);
            httpServerExchange.getResponseSender().send(gson.toJson(response));
        } catch (IllegalArgumentException ex) {
            handleException(httpServerExchange, ex, StatusCodes.BAD_REQUEST);
        } catch (ResourceNotFoundException ex) {
            handleException(httpServerExchange, ex, StatusCodes.NOT_FOUND);
        } catch (Exception ex) {
            handleException(httpServerExchange, ex, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }

    void handleException(HttpServerExchange httpServerExchange, Exception ex, int internalServerError) {
        httpServerExchange.setStatusCode(internalServerError);
        String message = Strings.nullToEmpty(ex.getMessage());
        httpServerExchange.getResponseSender().send(message);
    }

    abstract T getData(HttpServerExchange httpServerExchange) throws IOException;

    abstract Response invokeOkAction(T request);

    abstract void validateRequest(T request) throws IllegalArgumentException;
}
