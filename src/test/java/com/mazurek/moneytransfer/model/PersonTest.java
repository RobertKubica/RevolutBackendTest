package com.mazurek.moneytransfer.model;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PersonTest {


    @DataProvider
    public static Object[][] correctData() {
        return new Object[][]{
                {"John Smith", "+48123456789"},
                {"John Smith", "48123456789"},
                {"John Smith", "123456789"},
                {"John", "+48123456789"},
        };
    }

    @DataProvider
    public static Object[][] incorrectNames() {
        return new Object[][]{
                {"John_Smith"},
                {"J0hn"},
                {""},
                {"John."}
        };
    }

    @DataProvider
    public static Object[][] incorrectPhoneNumbers() {
        return new Object[][]{
                {"1 2"},
                {"+48 123 456 789"},
                {"++48123456789"},
                {"+48I23456789"},
                {""},
        };
    }

    @Test(dataProvider = "correctData")
    public void shouldCreatePersonWhenCorrectDataIsProvided(String name, String phoneNumber) {
        Person person = Person.create(name, phoneNumber);
        assertThat(person)
                .extracting(Person::getName, Person::getPhoneNumber)
                .containsExactly(name, phoneNumber);
    }

    @Test(dataProvider = "incorrectNames")
    public void shouldThrowIllegalArgumentExceptionOnIncorrectName(String name) {
        String correctPhoneNumber = "+48123456789";
        assertThatThrownBy(() -> Person.create(name, correctPhoneNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("%s is not correct name", name));
    }

    @Test(dataProvider = "incorrectPhoneNumbers")
    public void shouldThrowIllegalArgumentExceptionOnIncorrectPhoneNumbers(String phoneNumber) {
        String correctName = "John Smith";
        assertThatThrownBy(() -> Person.create(correctName, phoneNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("%s is not correct phone number", phoneNumber));
    }

    @Test
    public void personsWithSameNameAndPhoneNumberShouldBeEqual() {
        Person firstPerson = Person.create("abc", "123456789");
        Person secondPerson = Person.create("abc", "123456789");

        assertThat(firstPerson).isEqualTo(secondPerson);
    }
}