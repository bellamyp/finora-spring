package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_transaction_types"))
    private TransactionType type;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id", foreignKey = @ForeignKey(name = "fk_transactions_banks"))
    private Bank bank;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transactions_users"))
    private User user;

    public Transaction(LocalDate date, Double amount, TransactionType type, String notes, Bank bank, User user) {
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.notes = notes;
        this.bank = bank;
        this.user = user;
    }
}
