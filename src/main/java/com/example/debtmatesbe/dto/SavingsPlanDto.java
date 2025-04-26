package com.example.debtmatesbe.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SavingsPlanDto {
    private Long id;
    private String planName;
    private BigDecimal goalAmount;
    private BigDecimal currentAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextDepositDate;
    private String depositFrequency;
    private String planType;
    private Long userId;
}
