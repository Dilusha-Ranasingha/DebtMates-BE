package com.example.debtmatesbe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rotational_plan")
@Data
@NoArgsConstructor
public class RotationalPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private RotationalGroup group;

    @Column(nullable = false)
    private Integer monthNumber;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private Double amount;
}