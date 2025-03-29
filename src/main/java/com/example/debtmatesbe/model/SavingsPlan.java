package com.example.debtmatesbe.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "savings_plans")
public class SavingsPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "plan_type")
    private String planType;

    @Column(name = "goal_amount")
    private double goalAmount;

    @Column(name = "current_amount")
    private double currentAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "initial_deposit")
    private double initialDeposit;

    @Column(name = "frequency")
    private String frequency;
}