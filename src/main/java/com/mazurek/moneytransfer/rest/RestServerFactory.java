package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathTemplateHandler;

public class RestServerFactory {
    private final MoneyTransferController controller;

    public RestServerFactory(MoneyTransferController controller) {
        this.controller = controller;
    }

    public Undertow createServer() {
        PathTemplateHandler pathTemplateHandler = Handlers.pathTemplate();
        pathTemplateHandler.add("account", new CreateAccountHandler(controller));
        return Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(pathTemplateHandler).build();
    }

}
