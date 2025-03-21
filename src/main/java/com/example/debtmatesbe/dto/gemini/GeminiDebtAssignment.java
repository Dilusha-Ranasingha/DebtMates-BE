package com.example.debtmatesbe.dto.gemini;

import lombok.Data;

@Data
public class GeminiDebtAssignment {
    private Long memberId;
    private Long toWhoPay;
    private Double amountToPay;
}