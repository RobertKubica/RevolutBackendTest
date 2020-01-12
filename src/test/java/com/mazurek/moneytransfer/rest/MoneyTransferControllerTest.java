package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.model.Person;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoneyTransferControllerTest {

    @Test
    public void shouldCreateAccountCorrectly(){
        MoneyTransferController moneyTransferController = new MoneyTransferController();
        String accountId = moneyTransferController.createAccount("owner", "123456789");
        Account accountById = moneyTransferController.getAccountById(accountId).get();

        assertThat(accountById.getBalance()).isZero();
        assertThat(accountById.getOwner()).extracting(Person::getName, Person::getPhoneNumber)
                .containsExactly("owner", "123456789");
    }

    @Test
    public void shouldCreateOrderedIds(){
        MoneyTransferController moneyTransferController = new MoneyTransferController();
        String id1 = moneyTransferController.createAccount("first owner", "123456789");
        String id2 = moneyTransferController.createAccount("second owner", "123456789");

        assertThat(id1).isEqualTo("acc0");
        assertThat(id2).isEqualTo("acc1");
    }

    @Test
    public void shouldNotCreateAccountForExistingPerson(){
        MoneyTransferController moneyTransferController = new MoneyTransferController();
        moneyTransferController.createAccount("owner", "123456789");
        String duplicatedOwner = moneyTransferController.createAccount("owner", "123456789");

        assertThat(duplicatedOwner).isNull();

    }
}