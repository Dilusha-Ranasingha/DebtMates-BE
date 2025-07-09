package com.example.debtmatesbe.dto.group;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank(message = "Group name is required")
    private String groupName;

    @Min(value = 2, message = "Number of members must be at least 2")
    private int numMembers;

    @NotBlank(message = "Group description is required")
    private String groupDescription;
}


//This DTO defines the request body for POST /api/groups. It includes validation to
//ensure the group name and description are not blank, and the number of members is at least 2.