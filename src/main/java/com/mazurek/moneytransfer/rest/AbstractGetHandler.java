package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.Request;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractGetHandler<T extends Request> extends AbstractHandler<T> {
    public AbstractGetHandler(MoneyTransferController moneyTransferController) {
        super(moneyTransferController);
    }

    @Override
    final T getData(HttpServerExchange httpServerExchange) throws IOException {
        PathTemplateMatch pathMatch = httpServerExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        return createRequestFromParameters(pathMatch.getParameters());
    }

    abstract T createRequestFromParameters(Map<String, String> parameters);
}
