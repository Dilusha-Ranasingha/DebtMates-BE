package com.example.debtmatesbe.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateSavingsPlanDto {
    @NotBlank(message = "Plan name is required")
    private String title;

    @NotNull(message = "Goal amount is required")
    @Positive(message = "Goal amount must be positive")
    private BigDecimal goalAmount;

    @NotNull(message = "Current amount is required")
    @PositiveOrZero(message = "Current amount cannot be negative")
    private BigDecimal currentAmount;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Deposit frequency is required")
    private String depositFrequency;
}
