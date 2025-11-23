package com.bellamyphan.finora_spring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportBankId implements Serializable {
    private String report; // corresponds to report_id
    private String bank;   // corresponds to bank_id
}