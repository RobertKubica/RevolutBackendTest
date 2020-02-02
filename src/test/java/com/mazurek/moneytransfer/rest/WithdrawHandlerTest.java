package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.rest.requests.BalanceChangeRequest;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class WithdrawHandlerTest extends BalanceChangeHandlerTestBase {

    @Override
    protected void verifyControllerMethodInvoked(MoneyTransferController controller) {
        verify(controller, times(1)).withdraw("id0", BigDecimal.valueOf(1000));
    }

    @Override
    protected WithdrawHandler getHandler(MoneyTransferController controller) {
        return new WithdrawHandler(controller);
    }
}
