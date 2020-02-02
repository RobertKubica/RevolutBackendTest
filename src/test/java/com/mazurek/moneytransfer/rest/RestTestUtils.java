package com.mazurek.moneytransfer.rest;

import com.google.gson.Gson;
import com.mazurek.moneytransfer.rest.responses.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.nio.charset.Charset;

public class RestTestUtils {
    public static <T> T sendPost(CloseableHttpClient httpClient, String method, String request, Class<T> responseClass) throws IOException {
        CloseableHttpResponse response = sendPostRequest(httpClient, method, request);
        String responseBody = readResponseBody(response);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException(String.format("%d", response.getStatusLine().getStatusCode()));
        }
        return new Gson().fromJson(responseBody, responseClass);
    }

    static CloseableHttpResponse sendPostRequest(CloseableHttpClient httpClient, String method, String request) throws IOException {
        HttpPost post = new HttpPost("http://localhost:8080/" + method);
        StringEntity stringEntity = new StringEntity(request, ContentType.APPLICATION_JSON);
        post.setEntity(stringEntity);
        return httpClient.execute(post);
    }

    public static <T> T sendGet(CloseableHttpClient httpClient, String method, Class<T> responseClass) throws IOException {
        CloseableHttpResponse response = sendGetRequest(httpClient, method);
        String responseBody = readResponseBody(response);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException(String.format("%d", response.getStatusLine().getStatusCode()));
        }
        return new Gson().fromJson(responseBody, responseClass);
    }

    static CloseableHttpResponse sendGetRequest(CloseableHttpClient httpClient, String method) throws IOException {
        String uri = "http://localhost:8080/" + method;
        HttpGet httpGet = new HttpGet(uri);
        return httpClient.execute(httpGet);
    }

    public static String readResponseBody(CloseableHttpResponse response) throws IOException {
        return IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
    }

    public static class ErrorResponse implements Response {
        private final int statusCode;

        public ErrorResponse(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
