package com.example.debtmatesbe.dto.personalsaving;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositDto {
    @NotNull(message = "Deposit amount is required")
    @Positive(message = "Deposit amount must be positive")
    private BigDecimal amount;
}
