package com.mazurek.moneytransfer.rest;

import com.google.common.collect.ImmutableMap;
import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.model.Person;
import com.mazurek.moneytransfer.rest.requests.GetAccountInfoRequest;
import com.mazurek.moneytransfer.rest.responses.AccountViewResponse;
import com.mazurek.moneytransfer.rest.responses.Response;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetAccountInfoHandlerTest {

    @Test
    public void testInvokeOkAction() {
        MoneyTransferController controller = mock(MoneyTransferController.class);
        Account accountMock = mock(Account.class);
        Person owner = mock(Person.class);
        when(accountMock.getOwner()).thenReturn(owner);
        when(accountMock.getBalance()).thenReturn(BigDecimal.ZERO);
        when(controller.getAccount("id0")).thenReturn(accountMock);

        GetAccountInfoHandler handler = new GetAccountInfoHandler(controller);

        AccountViewResponse response = handler.invokeOkAction(new GetAccountInfoRequest("id0"));

        assertThat(response)
                .extracting(AccountViewResponse::getBalance, AccountViewResponse::getOwner)
                .containsExactly(BigDecimal.ZERO, owner);
    }

    @Test
    public void shouldCreateRequestCorrectly() {
        GetAccountInfoHandler handler = new GetAccountInfoHandler(null);
        GetAccountInfoRequest request = handler.createRequestFromParameters(ImmutableMap.of("id", "id0"));

        assertThat(request.getId()).isEqualTo("id0");
    }

    @Test
    public void shouldValidateRequestCorrectly() {
        GetAccountInfoHandler handler = new GetAccountInfoHandler(null);

        GetAccountInfoRequest request = new GetAccountInfoRequest("id0");

        assertThatCode(() -> handler.validateRequest(request)).doesNotThrowAnyException();
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        GetAccountInfoHandler handler = new GetAccountInfoHandler(null);

        GetAccountInfoRequest request = new GetAccountInfoRequest(null);

        assertThatThrownBy(() -> handler.validateRequest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Id cannot be null");

    }
}