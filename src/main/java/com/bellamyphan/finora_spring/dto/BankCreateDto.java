package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BankCreateDto {
    private String name;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private BankTypeEnum type; // Enum representing bank type
    private String userEmail;   // user email to link bank to
}
