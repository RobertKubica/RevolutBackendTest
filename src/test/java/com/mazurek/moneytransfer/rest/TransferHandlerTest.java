package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.rest.requests.TransferRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferHandlerTest {

    @Test
    public void shouldTransferMoneyWhenRequestIsCorrect() {
        MoneyTransferController controller = mock(MoneyTransferController.class);
        Account accountMock = mock(Account.class);
        when(accountMock.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        when(controller.getAccount("id0")).thenReturn(accountMock);

        TransferHandler handler = new TransferHandler(controller);
        TransferRequest request = new TransferRequest();
        request.setSourceAccountId("id0");
        request.setTargetAccountId("id1");
        request.setAmount(BigDecimal.valueOf(1000));
        BalanceResponse response = handler.invokeOkAction(request);

        verify(controller, times(1)).transfer("id0", "id1", BigDecimal.valueOf(1000));

        assertThat(response)
                .extracting(BalanceResponse::getAccountId, BalanceResponse::getBalance)
                .containsExactly("id0", BigDecimal.valueOf(1000));
    }

    @Test
    public void shouldParseJsonRequestCorrectly() {
        TransferHandler createAccountHandler = new TransferHandler(null);

        TransferRequest createAccountRequest = createAccountHandler.parseRequest("{\"sourceAccountId\": \"id0\",\"targetAccountId\": \"id1\",\"amount\": \"1000\"}");
        assertThat(createAccountRequest)
                .extracting(TransferRequest::getSourceAccountId, TransferRequest::getTargetAccountId, TransferRequest::getAmount)
                .containsExactly("id0", "id1", BigDecimal.valueOf(1000));
    }

    @Test
    public void shouldValidateRequestCorrectly() {
        TransferHandler createAccountHandler = new TransferHandler(null);

        TransferRequest createAccountRequest = new TransferRequest();
        createAccountRequest.setSourceAccountId("id0");
        createAccountRequest.setTargetAccountId("id1");
        createAccountRequest.setAmount(BigDecimal.valueOf(1000));

        assertThatCode(() -> createAccountHandler.validateRequest(createAccountRequest)).doesNotThrowAnyException();
    }

    @Test
    public void validationShouldThrowIllegalArgumentExceptionWhenSourceIdIsNull() {
        TransferHandler createAccountHandler = new TransferHandler(null);

        TransferRequest sourceNull = new TransferRequest();
        sourceNull.setTargetAccountId("id1");
        sourceNull.setAmount(BigDecimal.valueOf(1000));
        assertThatThrownBy(() -> createAccountHandler.validateRequest(sourceNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Source account cannot be null");

    }

    @Test
    public void validationShouldThrowIllegalArgumentExceptionWhenTargetIdIsNull() {
        TransferHandler createAccountHandler = new TransferHandler(null);

        TransferRequest targetNull = new TransferRequest();
        targetNull.setSourceAccountId("id0");
        targetNull.setAmount(BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> createAccountHandler.validateRequest(targetNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Target account cannot be null");

    }

    @Test
    public void validationShouldThrowIllegalArgumentExceptionWhenPhoneIsNull() {
        TransferHandler createAccountHandler = new TransferHandler(null);

        TransferRequest amountNull = new TransferRequest();
        amountNull.setSourceAccountId("id0");
        amountNull.setTargetAccountId("id1");

        assertThatThrownBy(() -> createAccountHandler.validateRequest(amountNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount cannot be null");

    }
}