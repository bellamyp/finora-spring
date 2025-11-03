package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "transaction_types")
@Data
@NoArgsConstructor
public class TransactionType {


    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;


    @Column(name = "type", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Transaction type is required")
    private String type;

    public TransactionType(String type) {
        this.type = type;
    }

    // Java-only enum helper
    @Transient
    public TransactionTypeEnum getEnum() {
        return TransactionTypeEnum.fromDisplayName(this.type);
    }
}