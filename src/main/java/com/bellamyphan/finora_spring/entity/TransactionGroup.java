package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Constructor without ID
    public TransactionGroup(Report report) {
        this.report = report;
    }
}
