package com.example.debtmatesbe.dto.group;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddMembersRequest {
    @NotEmpty(message = "At least one member must be added")
    private List<Long> userIds;
}

//This DTO defines the request body for POST /api/groups/{groupId}/members.
// It contains a list of userIds to add to the group.