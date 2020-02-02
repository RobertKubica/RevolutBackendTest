package com.mazurek.moneytransfer;

import com.mazurek.moneytransfer.model.Account;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

public class ConcurrencyTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        ConcurrentMoneyTransferController concurrentMoneyTransferController = new ConcurrentMoneyTransferController();
        String johnSmith = concurrentMoneyTransferController.createAccount("John Smith", "123456789");
        String adamSmith = concurrentMoneyTransferController.createAccount("Adam Smith", "987654321");

        concurrentMoneyTransferController.deposit(johnSmith, BigDecimal.valueOf(100000000));
        concurrentMoneyTransferController.deposit(adamSmith, BigDecimal.valueOf(100000000));
        Thread adamThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                concurrentMoneyTransferController.transfer(adamSmith, johnSmith, BigDecimal.valueOf(10));
                Account accountViewById = concurrentMoneyTransferController.getAccount(adamSmith);
                System.out.println("Adam balance:" + accountViewById.getBalance());
            }
            latch.countDown();
        });

        Thread johnThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                concurrentMoneyTransferController.transfer(johnSmith, adamSmith, BigDecimal.valueOf(10));
                Account accountViewById = concurrentMoneyTransferController.getAccount(johnSmith);
                System.out.println("John balance:" + accountViewById.getBalance());
            }
            latch.countDown();
        });

        adamThread.start();
        johnThread.start();

        latch.await();
        System.out.println("Final balances:");
        Account adamAccount = concurrentMoneyTransferController.getAccount(adamSmith);
        System.out.println("Adam balance:" + adamAccount.getBalance());
        Account johnAccount = concurrentMoneyTransferController.getAccount(johnSmith);
        System.out.println("John balance:" + johnAccount.getBalance());
    }
}
