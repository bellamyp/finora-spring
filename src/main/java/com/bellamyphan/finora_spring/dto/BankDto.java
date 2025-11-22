package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankDto {
    private String id;
    private String name;
    private BankTypeEnum type; // now returns enum
    private String email;
}
