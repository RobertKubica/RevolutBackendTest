package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;

public class RestServerFactory {
    private final MoneyTransferController controller;

    public RestServerFactory(MoneyTransferController controller) {
        this.controller = controller;
    }

    public Undertow createServer() {
        RoutingHandler routingHandler = Handlers.routing()
                .add("POST", "/account", new CreateAccountHandler(controller))
                .add("GET", "/account/{id}", new GetAccountInfoHandler(controller));
        return Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(routingHandler).build();
    }

}
