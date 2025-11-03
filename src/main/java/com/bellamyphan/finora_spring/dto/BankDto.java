package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.BankTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BankDto {
    private UUID id;
    private String name;
    private BankTypeEnum type; // now returns enum
    private String email;
}
