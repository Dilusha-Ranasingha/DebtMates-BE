package com.example.debtmatesbe.dto.debt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtResponse {
    private Long groupId;
    private String memberName;
    private Double contributed;
    private Double expected;
    private String toWhoPay;

    @JsonProperty("amountToPay")
    private Double amountToPay;
}