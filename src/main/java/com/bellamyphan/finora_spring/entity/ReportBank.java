package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "report_bank")
@IdClass(ReportBankId.class) // Composite PK class
public class ReportBank {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "report_id", nullable = false, foreignKey = @ForeignKey(name = "fk_report_bank_reports"))
    private Report report;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bank_id", nullable = false, foreignKey = @ForeignKey(name = "fk_report_bank_banks"))
    private Bank bank;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "Balance is required")
    private BigDecimal balance;

    // Constructor
    public ReportBank(Report report, Bank bank, BigDecimal balance) {
        this.report = report;
        this.bank = bank;
        this.balance = balance;
    }
}
