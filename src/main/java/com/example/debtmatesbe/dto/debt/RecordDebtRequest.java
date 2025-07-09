package com.example.debtmatesbe.dto.debt;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecordDebtRequest {

    @NotNull(message = "Total bill is required")
    @Positive(message = "Total bill must be positive")
    private Double totalBill;

    @NotEmpty(message = "Contributions list cannot be empty")
    private List<Contribution> contributions;

    @Getter
    @Setter
    public static class Contribution {
        @NotNull(message = "Member ID is required")
        private Long memberId;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private Double amount;
    }
}