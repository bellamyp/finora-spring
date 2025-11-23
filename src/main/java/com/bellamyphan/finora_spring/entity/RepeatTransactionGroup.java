package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "repeat_transaction_groups")
public class RepeatTransactionGroup {

    @Id
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_repeat_transaction_groups_transaction_groups"))
    private TransactionGroup group;

    // Constructor
    public RepeatTransactionGroup(TransactionGroup group) {
        this.group = group;
    }
}
