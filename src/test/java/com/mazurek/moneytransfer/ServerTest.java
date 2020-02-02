package com.mazurek.moneytransfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mazurek.moneytransfer.rest.RestTestUtils;
import com.mazurek.moneytransfer.rest.responses.AccountViewResponse;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import com.mazurek.moneytransfer.rest.responses.CreateAccountResponse;
import io.undertow.Undertow;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class ServerTest {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Undertow server;

    @BeforeMethod
    public void setup() {
        server = Main.createServer();
        server.start();
    }

    @AfterMethod
    public void tearDown() {
        server.stop();
    }

    @Test
    public void moneyTransferTest() throws Exception {
        SoftAssertions soft = new SoftAssertions();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        CreateAccountResponse caResponse = RestTestUtils.sendPost(httpClient, "account",
                "{\"owner\": \"John Smith\", \"phoneNumber\": \"+48123456789\"}",
                CreateAccountResponse.class);
        soft.assertThat(caResponse.getAccountId()).isEqualTo("acc0");


        BalanceResponse depositJohn = RestTestUtils.sendPost(httpClient, "deposit",
                String.format("{\"accountId\": \"%s\", \"amount\": 1500}",
                        caResponse.getAccountId()),
                BalanceResponse.class);
        soft.assertThat(depositJohn)
                .extracting(BalanceResponse::getAccountId, BalanceResponse::getBalance)
                .containsExactly("acc0", BigDecimal.valueOf(1500));

        CreateAccountResponse caResponse2 = RestTestUtils.sendPost(httpClient, "account",
                "{\"owner\": \"Adam Smith\", \"phoneNumber\": \"+48987654321\"}",
                CreateAccountResponse.class);
        soft.assertThat(caResponse2.getAccountId()).isEqualTo("acc1");

        BalanceResponse adamResponse = RestTestUtils.sendPost(httpClient, "transfer",
                String.format("{\"sourceAccountId\": \"%s\",\"targetAccountId\":\"%s\", \"amount\": 1200}",
                        caResponse.getAccountId(),
                        caResponse2.getAccountId()),
                BalanceResponse.class);
        soft.assertThat(adamResponse)
                .extracting(BalanceResponse::getAccountId, BalanceResponse::getBalance)
                .containsExactly("acc0", BigDecimal.valueOf(300));

        AccountViewResponse johnResponseAfterTransfer = RestTestUtils.sendGet(httpClient, "account/acc1", AccountViewResponse.class);
        soft.assertThat(johnResponseAfterTransfer.getBalance()).isEqualTo(BigDecimal.valueOf(1200));

        BalanceResponse adamResponseAfterWithdraw = RestTestUtils.sendPost(httpClient, "withdraw",
                String.format("{\"accountId\": \"%s\", \"amount\": 500}", caResponse2.getAccountId()),
                BalanceResponse.class);
        soft.assertThat(adamResponseAfterWithdraw)
                .extracting(BalanceResponse::getAccountId, BalanceResponse::getBalance)
                .containsExactly("acc1", BigDecimal.valueOf(700));


        soft.assertThatThrownBy(() -> RestTestUtils.sendGet(httpClient, "account/notexistingid", AccountViewResponse.class))
                .hasMessage("404");

        soft.assertAll();

    }

}



