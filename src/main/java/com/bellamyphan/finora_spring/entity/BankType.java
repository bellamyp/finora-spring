package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bank_types")
@Data
@NoArgsConstructor
public class BankType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, unique = true)
    @NotBlank(message = "Bank type is required")
    private String type;

    // Java-only enum helper (not persisted)
    @Transient
    public BankTypeEnum getBankEnum() {
        return BankTypeEnum.fromDisplayName(this.type);
    }

    public BankType(String type) {
        this.type = type;
    }
}
