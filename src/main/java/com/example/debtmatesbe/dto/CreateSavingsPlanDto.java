package com.example.debtmatesbe.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter

public class CreateSavingsPlanDto {
    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotBlank(message = "Plan type is required")
    private String planType;

    @NotNull(message = "Goal amount is required")
    @Positive(message = "Goal amount must be positive")
    private BigDecimal goalAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Initial deposit is required")
    @PositiveOrZero(message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;

    @NotBlank(message = "Deposit frequency is required")
    private String depositFrequency;

    @NotNull(message = "User ID is required")
    private Long userId;
}
