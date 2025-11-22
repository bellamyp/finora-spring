package com.bellamyphan.finora_spring.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BankTypeEnum {   // ✅ changed from class to enum

    CHECKING,
    SAVINGS,
    CREDIT,
    REWARDS;

    // Optional: find enum from name string (case-insensitive)
    public static BankTypeEnum fromName(String name) {
        for (BankTypeEnum bankEnum : values()) {
            if (bankEnum.name().equalsIgnoreCase(name)) {
                return bankEnum;
            }
        }
        throw new IllegalArgumentException("No matching BankTypeEnum for name: " + name);
    }
}
