package com.mazurek.moneytransfer;

import com.mazurek.moneytransfer.rest.RestServerFactory;
import io.undertow.Undertow;

public class Main {
    public static void main(String[] args) {
        Undertow server = createServer();

        server.start();

    }

    static Undertow createServer() {
        RestServerFactory restServerFactory = new RestServerFactory(new ConcurrentMoneyTransferController());
        return restServerFactory.createServer();
    }

}
