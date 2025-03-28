package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.dto.rotational.*;
import com.example.debtmatesbe.model.*;
import com.example.debtmatesbe.service.RotationalService;
import com.example.debtmatesbe.service.RotationalImageService;
import com.example.debtmatesbe.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rotational")
public class RotationalController {

    private final RotationalService rotationalService;
    private final RotationalImageService imageService;
    private final JwtUtil jwtUtil;

    public RotationalController(RotationalService rotationalService, RotationalImageService imageService, JwtUtil jwtUtil) {
        this.rotationalService = rotationalService;
        this.imageService = imageService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/groups")
    public ResponseEntity<?> createGroup(
            @RequestHeader("Authorization") String token,
            @RequestBody RotationalCreateGroupRequest request
    ) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User creator = getCurrentUser(username);
        RotationalGroup group = rotationalService.createGroup(
                request.getGroupName(),
                request.getGroupDescription(),
                request.getNumMembers(),
                creator
        );
        return ResponseEntity.ok(mapToGroupResponse(group, true));
    }

    @PutMapping("/groups/{groupId}")
    public ResponseEntity<?> editGroup(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId,
            @RequestBody RotationalUpdateGroupRequest request
    ) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = getCurrentUser(username);
        RotationalGroup group = rotationalService.editGroup(
                groupId,
                request.getGroupName(),
                request.getGroupDescription(),
                request.getNumMembers(),
                currentUser
        );
        return ResponseEntity.ok(mapToGroupResponse(group, true));
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId
    ) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = getCurrentUser(username);
        rotationalService.deleteGroup(groupId, currentUser);
        return ResponseEntity.ok("Group deleted successfully");
    }

    @PostMapping("/groups/{groupId}/members")
    public ResponseEntity<?> addMembers(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId,
            @RequestBody RotationalAddMembersRequest request
    ) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = getCurrentUser(username);
        RotationalGroup group = rotationalService.addMembers(groupId, request.getMemberIds(), currentUser);
        return ResponseEntity.ok(mapToGroupResponse(group, true));
    }

    @PostMapping("/groups/{groupId}/plan")
    public ResponseEntity<?> addPlan(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId,
            @RequestBody List<RotationalPlanRequest> planRequests
    ) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = getCurrentUser(username);
        List<RotationalPlan> plans = rotationalService.addPlan(groupId, planRequests, currentUser);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/groups")
    public ResponseEntity<?> getUserGroups(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = getCurrentUser(username);
        List<RotationalGroup> groups = rotationalService.getUserGroups(user);
        List<RotationalGroupResponse> responses = groups.stream()
                .map(group -> mapToGroupResponse(group, group.getCreator().getId().equals(user.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/groups/{groupId}/payments")
    public ResponseEntity<?> getPayments(@PathVariable Long groupId) {
        List<RotationalPayment> payments = rotationalService.getPayments(groupId);
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/payments/{paymentId}/slip")
    public ResponseEntity<?> uploadSlip(
            @RequestHeader("Authorization") String token,
            @PathVariable Long paymentId,
            @RequestBody byte[] slipData
    ) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = getCurrentUser(username);
        RotationalPayment payment = imageService.uploadSlip(paymentId, slipData, user.getId());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/payments/{paymentId}/slip")
    public ResponseEntity<?> getSlip(@PathVariable Long paymentId) {
        byte[] slip = imageService.getSlip(paymentId);
        return ResponseEntity.ok(slip);
    }

    private User getCurrentUser(String username) {
        User user = rotationalService.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    private RotationalGroupResponse mapToGroupResponse(RotationalGroup group, boolean isCreator) {
        RotationalGroupResponse response = new RotationalGroupResponse();
        response.setGroupId(group.getGroupId());
        response.setGroupName(group.getGroupName());
        response.setGroupDescription(group.getGroupDescription());
        response.setNumMembers(group.getNumMembers());
        response.setIsCreator(isCreator);
        return response;
    }
}