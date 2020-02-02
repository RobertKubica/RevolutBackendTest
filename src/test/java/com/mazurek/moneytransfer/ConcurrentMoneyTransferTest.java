package com.mazurek.moneytransfer;

import com.mazurek.moneytransfer.model.Account;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrentMoneyTransferTest {
    @Test
    public static void shouldBeThreadSafe() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        ConcurrentMoneyTransferController concurrentMoneyTransferController = new ConcurrentMoneyTransferController();
        String johnSmith = concurrentMoneyTransferController.createAccount("John Smith", "123456789");
        String adamSmith = concurrentMoneyTransferController.createAccount("Adam Smith", "987654321");

        concurrentMoneyTransferController.deposit(johnSmith, BigDecimal.valueOf(100000000));
        concurrentMoneyTransferController.deposit(adamSmith, BigDecimal.valueOf(100000000));
        Thread adamThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                concurrentMoneyTransferController.transfer(adamSmith, johnSmith, BigDecimal.valueOf(10));
            }
            latch.countDown();
        });

        Thread johnThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                concurrentMoneyTransferController.transfer(johnSmith, adamSmith, BigDecimal.valueOf(10));
            }
            latch.countDown();
        });

        adamThread.start();
        johnThread.start();

        latch.await();

        assertThat(concurrentMoneyTransferController.getAccount(adamSmith).getBalance()).isEqualTo(BigDecimal.valueOf(100000000));
        assertThat(concurrentMoneyTransferController.getAccount(johnSmith).getBalance()).isEqualTo(BigDecimal.valueOf(100000000));
    }
}
