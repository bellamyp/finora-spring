package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "banks")
@Data
@NoArgsConstructor
public class Bank {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Bank name is required")
    private String name;


    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_banks_bank_types"))
    private BankType type;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_banks_users"))
    private User user;

    public Bank(String name, LocalDate openingDate, LocalDate closingDate, BankType type, User user) {
        this.name = name;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.type = type;
        this.user = user;
    }
}
