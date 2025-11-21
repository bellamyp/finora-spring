package com.bellamyphan.finora_spring.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BankTypeEnum {   // ✅ changed from class to enum

    CHECKING("Checking"),
    SAVINGS("Savings"),
    CREDIT("Credit"),
    REWARDS("Rewards");

    private final String displayName;

    public static BankTypeEnum fromDisplayName(String displayName) {
        for (BankTypeEnum bankEnum : values()) {
            if (bankEnum.displayName.equalsIgnoreCase(displayName)) {
                return bankEnum;
            }
        }
        throw new IllegalArgumentException("No matching BankTypeEnum for display name: " + displayName);
    }

    public String getEnumName() {
        return this.name(); // e.g., "CHECKING"
    }
}
