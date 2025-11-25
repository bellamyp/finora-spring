package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankDto {
    private String id;
    private String name;
    private BankTypeEnum type; // now returns enum
    private String email;
    private BigDecimal balance;

    public BankDto(String id, String name, BankTypeEnum type, String email) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.email = email;
    }

    public BankDto(String id, String name, BankTypeEnum type, String email, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.email = email;
        this.balance = balance;
    }
}
