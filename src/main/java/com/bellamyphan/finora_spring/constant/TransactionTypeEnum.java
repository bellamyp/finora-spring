package com.bellamyphan.finora_spring.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionTypeEnum {
    INCOME("Income"),
    INCOME_TAX("Income Tax"),
    INVEST("Invest"),
    SAVINGS("Savings"),
    HEALTH("Health"),
    COLLEGE_WORK("College/Work"),
    CAR("Car"),
    GAS("Gas"),
    GROCERY("Grocery"),
    GOVERNMENT("Government"),
    HOUSING("Housing"),
    UTILITY("Utility"),
    PHONE("Phone"),
    PET("Pet"),
    ENTERTAINMENT("Entertainment"),
    MEAL("Meal"),
    SHOP("Shop"),
    CLOTHES("Clothes"),
    CHARITY("Charity"),
    CREDIT_PAYMENT("Credit Payment"),
    TRANSFER("Transfer"),
    OTHERS("Others");

    private final String displayName;

    TransactionTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public static TransactionTypeEnum fromDisplayName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.displayName.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid TransactionType name: " + name));
    }
}
