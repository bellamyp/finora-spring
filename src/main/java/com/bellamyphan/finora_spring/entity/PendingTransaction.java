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
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pending_transactions_transactions"))
    private Transaction transaction;

    // Constructor
    public PendingTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
