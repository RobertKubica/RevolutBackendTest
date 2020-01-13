package com.mazurek.moneytransfer.rest;

import com.mazurek.moneytransfer.MoneyTransferController;
import com.mazurek.moneytransfer.model.Account;
import com.mazurek.moneytransfer.model.AccountView;
import com.mazurek.moneytransfer.model.Person;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class MoneyTransferControllerTest {

    @BeforeMethod
    private void initialize() {
        moneyTransferController = new MoneyTransferController();
    }

    private MoneyTransferController moneyTransferController;

    @Test
    public void shouldCreateAccountCorrectly() {
        String accountId = createSampleAccount("owner");
        AccountView accountById = moneyTransferController.getAccountViewById(accountId).get();

        assertThat(accountById.getBalance()).isZero();
        assertThat(accountById.getOwner()).extracting(Person::getName, Person::getPhoneNumber)
                .containsExactly("owner", "123456789");
    }

    @Test
    public void shouldCreateOrderedIds() {
        String id1 = createSampleAccount("first owner");
        String id2 = createSampleAccount("second owner");

        assertThat(id1).isEqualTo("acc0");
        assertThat(id2).isEqualTo("acc1");
    }

    @Test
    public void shouldNotCreateAccountForExistingPerson() {
        createSampleAccount("owner");
        assertThatThrownBy(() -> createSampleAccount("owner"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Account already exist for provided person");

    }

    @Test
    public void shouldDepositMoneyCorrectly() {
        String accountId = createSampleAccount("owner");
        moneyTransferController.deposit(accountId, BigDecimal.valueOf(1400));
        AccountView accountViewById = moneyTransferController.getAccountViewById(accountId).get();

        assertThat(accountViewById.getBalance()).isEqualTo(BigDecimal.valueOf(1400));
    }

    @Test
    public void shouldForbidDepositingNegativeAmount() {
        String accountId = createSampleAccount("owner");
        assertThatThrownBy(() -> moneyTransferController.deposit(accountId, BigDecimal.valueOf(-1400)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount (-1400) must be positive");

    }

    @Test
    public void shouldWithdrawMoneyCorrectly() {
        String accountId = createSampleAccount("owner");
        moneyTransferController.deposit(accountId, BigDecimal.valueOf(100));
        moneyTransferController.withdraw(accountId, BigDecimal.valueOf(50));
        AccountView accountView = moneyTransferController.getAccountViewById(accountId).get();

        assertThat(accountView.getBalance()).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    public void shouldForbidWithdrawingAmountOverBalance() {
        String accountId = createSampleAccount("owner");
        moneyTransferController.deposit(accountId, BigDecimal.valueOf(50));
        assertThatThrownBy(() -> moneyTransferController.withdraw(accountId, BigDecimal.valueOf(100)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Balance is too low to withdraw: 100");
    }

    @Test
    public void shouldForbidWithdrawingNegativeAmount() {
        String accountId = createSampleAccount("owner");
        assertThatThrownBy(() -> moneyTransferController.withdraw(accountId, BigDecimal.valueOf(-1400)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount (-1400) must be positive");

    }

    @Test
    public void shouldForbidTransferringMoneyOverBalance() {
        String accountIdJohn = createSampleAccount("John Smith");
        String accountIdAdam = createSampleAccount("Adam Smith");
        moneyTransferController.deposit(accountIdJohn, BigDecimal.valueOf(1000));
        assertThatThrownBy(() -> moneyTransferController.transfer(accountIdJohn, accountIdAdam, BigDecimal.valueOf(2000)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Balance is too low to transfer: 2000");
    }

    @Test
    public void shouldTransferMoneyCorrectly() {
        String accountIdJohn = createSampleAccount("John Smith");
        String accountIdAdam = createSampleAccount("Adam Smith");
        assertThatThrownBy(() -> moneyTransferController.transfer(accountIdJohn, accountIdAdam, BigDecimal.valueOf(-100)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount (-100) must be positive");
    }


    private String createSampleAccount(String owner) {
        return moneyTransferController.createAccount(owner, "123456789");
    }
}