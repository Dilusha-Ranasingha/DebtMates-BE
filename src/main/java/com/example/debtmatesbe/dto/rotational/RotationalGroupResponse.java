package com.example.debtmatesbe.dto.rotational;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RotationalGroupResponse {
    private Long groupId;
    private String groupName;
    private String groupDescription;
    private int numMembers;
    private boolean isCreator;

    public void setIsCreator(boolean isCreator) {
        this.isCreator = isCreator;
    }
}