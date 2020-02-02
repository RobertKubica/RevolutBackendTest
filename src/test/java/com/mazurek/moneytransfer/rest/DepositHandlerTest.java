package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DepositHandlerTest extends BalanceChangeHandlerTestBase {
    @Override
    protected void verifyControllerMethodInvoked(MoneyTransferController controller) {
        verify(controller, times(1)).deposit("id0", BigDecimal.valueOf(1000));
    }

    @Override
    protected BalanceChangeHandler getHandler(MoneyTransferController controller) {
        return new DepositHandler(controller);
    }
}