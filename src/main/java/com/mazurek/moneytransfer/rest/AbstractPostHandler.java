package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.Request;
import io.undertow.server.HttpServerExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class AbstractPostHandler<T extends Request> extends AbstractHandler<T> {

    protected AbstractPostHandler(MoneyTransferController moneyTransferController) {
        super(moneyTransferController);
    }


    @Override
    T getData(HttpServerExchange httpServerExchange) throws IOException {
        String body = IOUtils.toString(httpServerExchange.getInputStream(), Charset.defaultCharset());
        return parseRequest(body);
    }

    abstract T parseRequest(String body);
}
