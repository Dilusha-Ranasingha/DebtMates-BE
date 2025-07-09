package com.example.debtmatesbe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "debt_details")
@Data
@NoArgsConstructor
public class DebtDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long debtId;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupDetails group;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    private Double amountContributed;

    private Double amountExpected;

    @ManyToOne
    @JoinColumn(name = "to_who_pay_id")
    private User toWhoPay;

    private Double amountToPay;
}