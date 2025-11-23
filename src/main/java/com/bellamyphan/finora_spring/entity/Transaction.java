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
    private String id; // Java will generate NanoID 10-char

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_transaction_groups"))
    @NotNull(message = "Transaction group is required")
    private TransactionGroup group;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @Column(name = "notes", length = 255)
    private String notes;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bank_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_banks"))
    @NotNull(message = "Bank is required")
    private Bank bank;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "brand_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_brands"))
    @NotNull(message = "Brand is required")
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_transaction_types"))
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
}
