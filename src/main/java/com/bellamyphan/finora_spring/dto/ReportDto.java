package com.bellamyphan.finora_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    private String id;
    private String userId;
    private LocalDate month;
    private boolean isPosted;
}
