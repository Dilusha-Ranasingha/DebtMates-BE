package com.example.debtmatesbe.dto;

import com.example.debtmatesbe.model.SavingsPlan.PlanType;
import com.example.debtmatesbe.model.SavingsPlan.DepositFrequency;
import lombok.Data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SavingsPlanDTO {
    @NotBlank
    private String title;

    @NotNull
    @Min(1)
    private Double goalAmount;

    private Double currentAmount = 0.0;

    @NotNull
    private LocalDate startDate = LocalDate.now();

    @NotNull
    @Future
    private LocalDate endDate;

    @NotNull
    private DepositFrequency depositFrequency;

    @NotNull
    private PlanType planType;

}
