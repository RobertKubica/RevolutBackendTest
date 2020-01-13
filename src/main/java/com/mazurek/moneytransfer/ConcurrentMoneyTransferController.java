package com.mazurek.moneytransfer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConcurrentMoneyTransferController extends MoneyTransferController {

    public Map<String, Lock> lockMap = initializeMap();

    @Override
    protected <K, V> Map<K, V> initializeMap() {
        return new ConcurrentHashMap<>();
    }

    @Override
    public String createAccount(String ownerName, String ownerPhoneNumber) {
        String account = super.createAccount(ownerName, ownerPhoneNumber);
        lockMap.put(account, new ReentrantLock());
        return account;
    }

    @Override
    public void deposit(String id, BigDecimal amount) {
        Lock lock = lockMap.get(id);
        try {
            lock.lock();
            super.deposit(id, amount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void withdraw(String id, BigDecimal amount) {
        Lock lock = lockMap.getOrDefault(id, new ReentrantLock());
        try {
            lock.lock();
            super.withdraw(id, amount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void transfer(String sourceId, String targetId, BigDecimal amount) {
        List<Lock> locks = Stream.of(sourceId, targetId)
                .sorted()
                .map(lockMap::get)
                .collect(Collectors.toList());

        try {
            locks.forEach(Lock::lock);
            super.transfer(sourceId, targetId, amount);

        } finally {
            locks.forEach(Lock::unlock);
        }
    }
}
