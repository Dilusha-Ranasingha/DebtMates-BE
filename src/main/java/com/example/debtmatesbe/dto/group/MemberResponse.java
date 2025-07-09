package com.example.debtmatesbe.dto.group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponse {
    private Long id;
    private String username;
}


/*
    This DTO will be used to return a list of members in the response for GET /api/groups/{groupId}/members
*/