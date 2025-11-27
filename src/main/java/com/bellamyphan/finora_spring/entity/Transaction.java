package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // Java-generated NanoID

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_transaction_groups"))
    @NotNull(message = "Transaction group is required")
    private TransactionGroup group;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id",
            foreignKey = @ForeignKey(name = "fk_transactions_banks"))
    private Bank bank;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id",
            foreignKey = @ForeignKey(name = "fk_transactions_brands"))
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id",
            foreignKey = @ForeignKey(name = "fk_transactions_locations"))
    private Location location; // optional during draft

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id",
            foreignKey = @ForeignKey(name = "fk_transactions_transaction_types"))
    private TransactionType type;
}
