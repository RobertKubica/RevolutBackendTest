package com.mazurek.moneytransfer.rest;

import com.google.common.base.Strings;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.exceptions.ResourceNotFoundException;
import com.mazurek.moneytransfer.rest.requests.Request;
import com.mazurek.moneytransfer.rest.responses.Response;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.function.Function;

import static com.mazurek.moneytransfer.rest.RestTestUtils.readResponseBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AbstractHandlerTest {

    private Undertow server;

    public void runServerThread(Function<Boolean, Response> responseSupplier) {
        server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new StubHandler(responseSupplier)).build();
        server.start();
    }

    @AfterMethod
    public void closeServer() {
        server.stop();
    }

    @Test
    public void shouldHandleRequestCorrectly() throws IOException {
        runServerThread(OkResponse::new);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = RestTestUtils.sendPostRequest(httpClient, "", "");

        assertThat(closeableHttpResponse.getStatusLine().getStatusCode()).isEqualTo(StatusCodes.OK);
        assertThat(readResponseBody(closeableHttpResponse)).isEqualToIgnoringWhitespace("{\"validated\":true}");
    }

    @DataProvider
    public static Object[][] errors() {
        return new Object[][]{
                {new IllegalArgumentException("illegal argument"), StatusCodes.BAD_REQUEST},
                {new ResourceNotFoundException("not found"), StatusCodes.NOT_FOUND},
                {new RuntimeException("other exception"), StatusCodes.INTERNAL_SERVER_ERROR}
        };
    }

    @Test(dataProvider = "errors")
    public void shouldReturnCorrectErrorCodes(RuntimeException ex, int statusCode) throws IOException {
        runServerThread($ -> {
            throw ex;
        });

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = RestTestUtils.sendPostRequest(httpClient, "", "");

        assertThat(closeableHttpResponse.getStatusLine().getStatusCode()).isEqualTo(statusCode);
        assertThat(readResponseBody(closeableHttpResponse)).isEqualTo(Strings.nullToEmpty(ex.getMessage()));
    }


    private static class StubHandler extends AbstractHandler<Request> {
        private final Function<Boolean, Response> invokeAction;

        private boolean validated = false;

        public StubHandler(Function<Boolean, Response> invokeAction) {
            super(mock(MoneyTransferController.class));
            this.invokeAction = invokeAction;
        }

        @Override
        Request getData(HttpServerExchange httpServerExchange) throws IOException {
            return mock(Request.class);
        }

        @Override
        Response invokeOkAction(Request request) {
            return invokeAction.apply(validated);
        }

        @Override
        void validateRequest(Request request) throws IllegalArgumentException {
            validated = true;
        }

        public boolean isValidated() {
            return validated;
        }
    }

    private static class OkResponse implements Response {
        private final boolean validated;

        private OkResponse(boolean validated) {
            this.validated = validated;
        }
    }
}