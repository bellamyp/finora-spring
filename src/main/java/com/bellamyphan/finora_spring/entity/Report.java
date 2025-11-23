package com.bellamyphan.finora_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "month", nullable = false)
    @NotNull(message = "Month is required")
    private LocalDate month; // First day of the month

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reports_users"))
    private User user;

    @Column(name = "is_posted", nullable = false)
    private boolean isPosted = false;

    // Constructor without ID (Java can generate NanoID separately)
    public Report(LocalDate month, User user, boolean isPosted) {
        this.month = month;
        this.user = user;
        this.isPosted = isPosted;
    }
}
