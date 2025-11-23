package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "report_type")
@IdClass(ReportTypeId.class) // Composite PK class
public class ReportType {

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "report_id", nullable = false, foreignKey = @ForeignKey(name = "fk_report_type_reports"))
    private Report report;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_report_type_transaction_types"))
    private TransactionType type;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    // Constructor without composite key (report & type assigned separately)
    public ReportType(Report report, TransactionType type, BigDecimal totalAmount) {
        this.report = report;
        this.type = type;
        this.totalAmount = totalAmount;
    }
}
