package com.mazurek.moneytransfer;

import com.mazurek.moneytransfer.rest.RestServerFactory;
import io.undertow.Undertow;

public class Main {
    public static void main(String[] args) {
        RestServerFactory restServerFactory = new RestServerFactory(new MoneyTransferController());
        Undertow server = restServerFactory.createServer();

        server.start();

    }

}
