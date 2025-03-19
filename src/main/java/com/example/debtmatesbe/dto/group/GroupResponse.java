package com.example.debtmatesbe.dto.group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupResponse {
    private Long groupId;
    private String groupName;
    private int numMembers;
    private String groupDescription;
    private boolean isCreator;
}


//This DTO defines the response for GET /api/users/me/groups. It includes
// a isCreator flag to indicate if the logged-in user can edit the group.