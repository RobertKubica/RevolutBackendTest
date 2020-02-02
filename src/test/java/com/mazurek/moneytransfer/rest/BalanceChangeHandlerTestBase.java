package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.rest.requests.BalanceChangeRequest;
import com.mazurek.moneytransfer.rest.responses.BalanceResponse;
import com.mazurek.moneytransfer.rest.responses.Response;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BalanceChangeHandlerTestBase {

    @Test
    public void shouldChangeBalanceWhenRequestIsCorrect() {
        MoneyTransferController controller = mock(MoneyTransferController.class);
        Account accountMock = mock(Account.class);
        when(accountMock.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        when(controller.getAccount("id0")).thenReturn(accountMock);

        BalanceChangeHandler handler = getHandler(controller);

        BalanceChangeRequest request = createRequest();
        BalanceResponse response = handler.invokeOkAction(request);
        verifyControllerMethodInvoked(controller);
        assertThat(response)
                .extracting(BalanceResponse::getAccountId, BalanceResponse::getBalance)
                .containsExactly("id0", BigDecimal.valueOf(1000));

    }

    private BalanceChangeRequest createRequest() {
        BalanceChangeRequest balanceChangeRequest = new BalanceChangeRequest();

        balanceChangeRequest.setAccountId("id0");
        balanceChangeRequest.setAmount(BigDecimal.valueOf(1000));

        return balanceChangeRequest;
    }

    @Test
    public void shouldInvokeChangeBalanceCorrectly() {
        MoneyTransferController controller = mock(MoneyTransferController.class);
        BalanceChangeHandler handler = getHandler(controller);

        BalanceChangeRequest balanceChangeRequest = createRequest();

        handler.invokeBalanceChange(balanceChangeRequest);

        verifyControllerMethodInvoked(controller);
    }

    protected abstract void verifyControllerMethodInvoked(MoneyTransferController controller);

    protected abstract BalanceChangeHandler getHandler(MoneyTransferController controller);

    @Test
    public void shouldParseRequestCorrectly() {
        BalanceChangeHandler depositHandler = getHandler(null);
        BalanceChangeRequest balanceChangeRequest = depositHandler.parseRequest("{\"accountId\": \"id0\",\"amount\": 1000}");

        assertThat(balanceChangeRequest)
                .extracting(BalanceChangeRequest::getAccountId, BalanceChangeRequest::getAmount)
                .containsExactly("id0", BigDecimal.valueOf(1000));
    }

    @Test
    public void shouldValidateRequestCorrectly() {
        BalanceChangeHandler depositHandler = getHandler(null);

        BalanceChangeRequest depositRequest = new BalanceChangeRequest();
        depositRequest.setAccountId("id0");
        depositRequest.setAmount(BigDecimal.valueOf(1000));

        assertThatCode(() -> depositHandler.validateRequest(depositRequest)).doesNotThrowAnyException();
    }

    @Test
    public void validationShouldThrowIllegalArgumentExceptionWhenOwnerIsNull() {
        BalanceChangeHandler depositHandler = getHandler(null);

        BalanceChangeRequest amountNull = new BalanceChangeRequest();
        amountNull.setAccountId("id0");

        assertThatThrownBy(() -> depositHandler.validateRequest(amountNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount cannot be null");

    }

    @Test
    public void validationShouldThrowIllegalArgumentExceptionWhenPhoneIsNull() {
        BalanceChangeHandler depositHandler = getHandler(null);

        BalanceChangeRequest accountIdNull = new BalanceChangeRequest();
        accountIdNull.setAmount(BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> depositHandler.validateRequest(accountIdNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Account id cannot be null");

    }
}
