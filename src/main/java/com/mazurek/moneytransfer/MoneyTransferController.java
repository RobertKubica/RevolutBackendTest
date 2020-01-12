package com.mazurek.moneytransfer;

import com.google.common.base.Preconditions;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.model.Person;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MoneyTransferController {
    private AtomicInteger idCounter = new AtomicInteger(0);

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<Person, Account> personAccountMap = new HashMap<>();

    public String createAccount(String ownerName, String ownerPhoneNumber) {
        Person person = Person.create(ownerName, ownerPhoneNumber);
        if (personAccountMap.containsKey(person)) {
            throw new IllegalArgumentException("Account already exist for provided person");
        }
        Account account = new Account(person);
        String newId = createNewId();
        accounts.put(newId, account);
        personAccountMap.put(person, account);
        return newId;
    }

    public Optional<Account> getAccountById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public void deposit(String id, BigDecimal amount){
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, String.format("Amount (%s) must be positive", amount));
        Account account = accounts.get(id);
        account.setBalance(account.getBalance().add(amount));
    }

    public void withdraw(String id, BigDecimal amount){
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, String.format("Amount (%s) must be positive", amount));
        Account account = accounts.get(id);
        if(account.getBalance().compareTo(amount)<0){
            throw new IllegalArgumentException(String.format("Balance is too low to withdraw: %s", amount));
        }
        account.setBalance(account.getBalance().subtract(amount));
    }

    public void transfer(String sourceId, String targetId, BigDecimal amount) {
        withdraw(sourceId,amount);
        deposit(targetId,amount);
    }

    private String createNewId() {
        return String.format("acc%d", idCounter.getAndIncrement());
    }
}
