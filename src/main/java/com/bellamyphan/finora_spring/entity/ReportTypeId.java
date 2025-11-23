package com.bellamyphan.finora_spring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportTypeId implements Serializable {
    private String report;
    private String type;
}
