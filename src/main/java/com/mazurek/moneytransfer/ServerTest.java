package com.mazurek.moneytransfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import com.mazurek.moneytransfer.rest.responses.CreateAccountResponse;
import io.undertow.Undertow;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.Charset;

public class ServerTest {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> {
            Undertow server = Main.createServer();
            server.start();
        });
        try {
            serverThread.start();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            while (!checkIfServerWorking(httpClient)) {
                System.out.println("Server not started... trying again");
            }

            CreateAccountResponse caResponse = sendPost(httpClient, "account",
                    "{\"owner\": \"John Smith\", \"phoneNumber\": \"+48123456789\"}",
                    CreateAccountResponse.class);
            sendPost(httpClient, "deposit",
                    String.format("{\"accountId\": \"%s\", \"amount\": 1500}",
                            caResponse.getAccountId()),
                    BalanceResponse.class);

            CreateAccountResponse caResponse2 = sendPost(httpClient, "account",
                    "{\"owner\": \"Adam Smith\", \"phoneNumber\": \"+48987654321\"}",
                    CreateAccountResponse.class);
            sendPost(httpClient, "transfer",
                    String.format("{\"sourceAccountId\": \"%s\",\"targetAccountId\":\"%s\", \"amount\": 1200}",
                            caResponse.getAccountId(),
                            caResponse2.getAccountId()),
                    BalanceResponse.class);

            sendPost(httpClient, "withdraw",
                    String.format("{\"accountId\": \"%s\", \"amount\": 500}", caResponse2.getAccountId()),
                    BalanceResponse.class);

            sendGet(httpClient, "account/" + caResponse2.getAccountId());

            sendGet(httpClient, "account/notexistingid");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }

    static <T> T sendPost(CloseableHttpClient httpClient, String method, String request, Class<T> responseClass) throws IOException {
        HttpPost post = new HttpPost("http://localhost:8080/" + method);
        StringEntity stringEntity = new StringEntity(request, ContentType.APPLICATION_JSON);
        post.setEntity(stringEntity);
        System.out.println(String.format("Sending request: %s", request));
        CloseableHttpResponse response = httpClient.execute(post);
        String responseBody = readResponseBody(response);
        System.out.println(String.format("Received response: %s", responseBody));
        if (response.getStatusLine().getStatusCode() != 200) {
            return null;
        }
        return gson.fromJson(responseBody, responseClass);
    }

    static String sendGet(CloseableHttpClient httpClient, String method) throws IOException {
        String uri = "http://localhost:8080/" + method;
        HttpGet httpGet = new HttpGet(uri);
        System.out.println("Sending GET request to: " + uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String responseBody = readResponseBody(response);
        System.out.println(String.format("Received response: %s", responseBody));
        return responseBody;
    }

    private static boolean checkIfServerWorking(CloseableHttpClient httpClient) {
        HttpGet httpGet = new HttpGet("http://localhost:8080");
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String responseBody = readResponseBody(response);
            return responseBody.equals("OK");
        } catch (IOException e) {
            return false;
        }
    }

    private static String readResponseBody(CloseableHttpResponse response) throws IOException {
        return IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
    }


}
