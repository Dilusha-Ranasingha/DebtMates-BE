package com.example.debtmatesbe.dto.rotational;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RotationalUpdateGroupRequest {
    @NotBlank(message = "Group name is required")
    private String groupName;

    @NotBlank(message = "Group description is required")
    private String groupDescription;

    @Min(value = 2, message = "Number of members must be at least 2")
    private int numMembers;
}