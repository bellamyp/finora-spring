package com.bellamyphan.finora_spring.entity;

import java.util.Arrays;

public enum TransactionTypeEnum {
    INCOME(1, "Income"),
    INCOME_TAX(2, "Income Tax"),
    INVEST(3, "Invest"),
    SAVINGS(4, "Savings"),
    HEALTH(5, "Health"),
    COLLEGE_WORK(6, "College/Work"),
    CAR(7, "Car"),
    GAS(8, "Gas"),
    GROCERY(9, "Grocery"),
    GOVERNMENT(10, "Government"),
    HOUSING(11, "Housing"),
    UTILITY(12, "Utility"),
    PHONE(13, "Phone"),
    PET(14, "Pet"),
    ENTERTAINMENT(15, "Entertainment"),
    MEAL(16, "Meal"),
    SHOP(17, "Shop"),
    CLOTHES(18, "Clothes"),
    CHARITY(19, "Charity");

    private final int id;
    private final String displayName;

    TransactionTypeEnum(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TransactionTypeEnum fromId(int id) {
        return Arrays.stream(values())
                .filter(e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid TransactionType ID: " + id));
    }

    public static TransactionTypeEnum fromDisplayName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.displayName.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid TransactionType name: " + name));
    }
}
