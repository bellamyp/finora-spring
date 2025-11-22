package com.bellamyphan.finora_spring.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionTypeEnum {
    INCOME,
    INCOME_TAX,
    INVEST,
    SAVINGS,
    HEALTH,
    COLLEGE_WORK,
    CAR,
    GAS,
    GROCERY,
    GOVERNMENT,
    HOUSING,
    UTILITY,
    PHONE,
    PET,
    ENTERTAINMENT,
    MEAL,
    SHOP,
    CLOTHES,
    CHARITY,
    CREDIT_PAYMENT,
    TRANSFER,
    OTHERS;

    // Optional: parse from string (case-insensitive)
    public static TransactionTypeEnum fromName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid TransactionTypeEnum name: " + name));
    }
}
