package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pending_transactions")
public class PendingTransaction {

    @Id
    private String transactionId; // Primary key, matches Transaction.id

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId // Maps transactionId from the Transaction entity
    @JoinColumn(name = "transaction_id", foreignKey = @ForeignKey(name = "fk_pending_transactions_transactions"))
    private Transaction transaction;

    // Constructor
    public PendingTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
