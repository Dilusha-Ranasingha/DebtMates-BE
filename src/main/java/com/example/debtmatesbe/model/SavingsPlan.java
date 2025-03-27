package com.example.debtmatesbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "savings_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SavingsPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double goalAmount;

    @Column(nullable = false)
    private Double currentAmount = 0.0;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate nextDepositDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositFrequency depositFrequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status = PlanStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public enum DepositFrequency {
        DAILY, WEEKLY, BIWEEKLY, MONTHLY
    }

    public enum PlanType {
        EMERGENCY, VACATION, RETIREMENT, MAJOR_PURCHASE, CUSTOM
    }

    public enum PlanStatus {
        ACTIVE, COMPLETED, CANCELLED
    }

    public int getProgressPercentage() {
        if (goalAmount <= 0) return 0;
        return (int) Math.min(100, Math.round((currentAmount / goalAmount) * 100));
    }
}
