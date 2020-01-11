package com.mazurek.moneytransfer.model;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

public class Person {

    private final String name;
    private final String phoneNumber;

    private Person(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public static Person create(String name, String phoneNumber){
        validateName(name);
        validatePhoneNumber(phoneNumber);
        return new Person(name, phoneNumber);
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    private static void validateName(String name) {
        Preconditions.checkNotNull(name, "Person's name cannot be null");
        Pattern pattern = Pattern.compile("[A-Za-z]+( [A-Za-z]+)?");
        Preconditions.checkArgument(pattern.matcher(name).matches(), String.format("%s is not correct name", name));
    }

    private static void validatePhoneNumber(String phoneNumber) {
        Preconditions.checkNotNull(phoneNumber, "Person's phone number cannot be null");
        Pattern pattern = Pattern.compile("(\\+?\\d{2})?\\d{8,9}");
        Preconditions.checkArgument(pattern.matcher(phoneNumber).matches(), String.format("%s is not correct phone number", phoneNumber));

    }
}
