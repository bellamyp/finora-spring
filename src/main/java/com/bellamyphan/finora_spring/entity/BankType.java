package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "bank_types")
@Data
@NoArgsConstructor
public class BankType {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @Column(name = "type", nullable = false, unique = true, length = 50)
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
