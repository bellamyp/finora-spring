package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transaction_groups")
public class TransactionGroup {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_transaction_groups_transaction_types"))
    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", foreignKey = @ForeignKey(name = "fk_transaction_groups_brands"))
    private Brand brand;

    @Column(name = "date", nullable = false)
    @NotNull(message = "Transaction date is required")
    private LocalDate date;

    @Column(name = "notes", length = 255)
    private String notes;

    // Constructor without ID (Java can generate NanoID separately)
    public TransactionGroup(TransactionType type, Brand brand, LocalDate date, String notes) {
        this.type = type;
        this.brand = brand;
        this.date = date;
        this.notes = notes;
    }
}
