package com.example.debtmatesbe.dto.rotational;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RotationalAddMembersRequest {
    @NotEmpty(message = "At least one member must be added")
    private List<Long> memberIds;
}