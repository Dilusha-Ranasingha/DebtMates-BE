package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.dto.group.AddMembersRequest;
import com.example.debtmatesbe.dto.group.CreateGroupRequest;
import com.example.debtmatesbe.dto.group.GroupResponse;
import com.example.debtmatesbe.dto.group.UpdateGroupRequest;
import com.example.debtmatesbe.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse response = groupService.createGroup(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(@PathVariable Long groupId,
                                                     @Valid @RequestBody UpdateGroupRequest request) {
        GroupResponse response = groupService.updateGroup(groupId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<GroupResponse>> getUserGroups() {
        List<GroupResponse> groups = groupService.getUserGroups();
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMembers(@PathVariable Long groupId,
                                           @Valid @RequestBody AddMembersRequest request) {
        groupService.addMembers(groupId, request);
        return ResponseEntity.ok().build();
    }
}



/*
This controller defines the Group Management APIs:
    POST /api/groups: Creates a new group.
    PUT /api/groups/{groupId}: Updates an existing group.
    GET /api/groups/me: Retrieves the logged-in user’s groups.
    POST /api/groups/{groupId}/members: Adds members to a group.
*/