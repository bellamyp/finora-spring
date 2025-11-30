package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDto {
    private String id;
    private String groupId;
    private String name;
    private BankTypeEnum type;
    private String email;
    private BigDecimal balance;
}
