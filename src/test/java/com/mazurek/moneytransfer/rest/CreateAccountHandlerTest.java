package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.CreateAccountRequest;
import com.mazurek.moneytransfer.rest.responses.CreateAccountResponse;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateAccountHandlerTest {

    @Test
    public void shouldCreateAccountWhenRequestIsCorrect() {
        MoneyTransferController moneyTransferController = spy(MoneyTransferController.class);
        when(moneyTransferController.createAccount("owner", "123456789")).thenReturn("id0");
        CreateAccountHandler createAccountHandler = new CreateAccountHandler(moneyTransferController);
        CreateAccountRequest request = new CreateAccountRequest();
        request.setOwner("owner");
        request.setPhoneNumber("123456789");

        CreateAccountResponse response = createAccountHandler.invokeOkAction(request);

        verify(moneyTransferController, times(1)).createAccount("owner", "123456789");
        assertThat(response.getAccountId()).isEqualTo("id0");
    }

    @Test
    public void shouldParseJsonRequestCorrectly() {
        CreateAccountHandler createAccountHandler = new CreateAccountHandler(null);

        CreateAccountRequest createAccountRequest = createAccountHandler.parseRequest("{\"owner\": \"owner\",\"phoneNumber\": \"123456789\"}");
        assertThat(createAccountRequest)
                .extracting(CreateAccountRequest::getOwner, CreateAccountRequest::getPhoneNumber)
                .containsExactly("owner", "123456789");
    }

    @Test
    public void shouldValidateRequestCorrectly() {
        CreateAccountHandler createAccountHandler = new CreateAccountHandler(null);

        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setPhoneNumber("123");
        createAccountRequest.setOwner("own");

        assertThatCode(() -> createAccountHandler.validateRequest(createAccountRequest)).doesNotThrowAnyException();
    }

    @Test
    public void validationShouldThrowIllegalArgumentExceptionWhenOwnerIsNull(){
        CreateAccountHandler createAccountHandler = new CreateAccountHandler(null);

        CreateAccountRequest ownerNull = new CreateAccountRequest();
        ownerNull.setPhoneNumber("123");
        assertThatThrownBy(() -> createAccountHandler.validateRequest(ownerNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Owner cannot be null");

    }

    @Test
    public void validationShouldThrowIllegalArgumentExceptionWhenPhoneIsNull(){
        CreateAccountHandler createAccountHandler = new CreateAccountHandler(null);

        CreateAccountRequest phoneNull = new CreateAccountRequest();
        phoneNull.setOwner("own");
        assertThatThrownBy(() -> createAccountHandler.validateRequest(phoneNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Phone number cannot be null");

    }

}