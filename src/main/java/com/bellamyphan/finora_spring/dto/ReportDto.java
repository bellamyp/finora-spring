package com.bellamyphan.finora_spring.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportDto {

    private String id;
    private String userId;
    private LocalDate month;
    private boolean isPosted;
}
