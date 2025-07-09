package com.example.debtmatesbe.dto.rotational;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RotationalPlanRequest {
    private Integer monthNumber;
    private Long recipientId;
    private Double amount;
}