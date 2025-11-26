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
    private String groupId; // Primary key, matches TransactionGroup.id

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId // Maps groupId from TransactionGroup entity
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_repeat_transaction_groups_transaction_groups"))
    private TransactionGroup group;

    public RepeatTransactionGroup(TransactionGroup group) {
        this.group = group;
    }
}
