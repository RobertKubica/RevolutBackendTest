package com.mazurek.moneytransfer;

import com.mazurek.moneytransfer.model.AccountView;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentMoneyTransferController extends MoneyTransferController {

    public Map<String, Lock> lockMap = initializeMap();

    @Override
    protected <K, V> Map<K, V> initializeMap() {
        return new ConcurrentHashMap<>();
    }

    @Override
    public String createAccount(String ownerName, String ownerPhoneNumber) {
        String account = super.createAccount(ownerName, ownerPhoneNumber);
        lockMap.put(account,new ReentrantLock());
        return account;
    }

    @Override
    public void deposit(String id, BigDecimal amount) {
        Lock lock = lockMap.get(id);
        lock.lock();
        super.deposit(id, amount);
        lock.unlock();
    }

    @Override
    public void withdraw(String id, BigDecimal amount) {
        Lock lock = lockMap.get(id);
        lock.lock();
        super.withdraw(id, amount);
        lock.unlock();
    }

    @Override
    public void transfer(String sourceId, String targetId, BigDecimal amount) {
        Lock sourceLock = lockMap.get(sourceId);
        Lock targetLock = lockMap.get(targetId);
        sourceLock.lock();
        targetLock.lock();
        super.transfer(sourceId, targetId, amount);
        sourceLock.unlock();
        targetLock.unlock();
    }
}
