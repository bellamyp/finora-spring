package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transaction_groups")
public class TransactionGroup {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", foreignKey = @ForeignKey(name = "fk_transaction_groups_reports"))
    private Report report;

    // NEW: link to transactions
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    // Constructor without ID
    // Todo: Change to all args constructor
    public TransactionGroup(Report report) {
        this.report = report;
    }
}
