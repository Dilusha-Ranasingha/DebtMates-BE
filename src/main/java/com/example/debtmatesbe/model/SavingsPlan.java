package com.example.debtmatesbe.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Table(name = "savings_plans")
@Getter
@Setter
public class SavingsPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "plan_type")
    private String planType;

    @Column(name = "goal_amount")
    private BigDecimal goalAmount;

    @Column(name = "current_amount")
    private BigDecimal currentAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "initial_deposit")
    private BigDecimal initialDeposit;

    @Column(name = "deposit_frequency")
    private String depositFrequency;

    private LocalDate nextDepositDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

