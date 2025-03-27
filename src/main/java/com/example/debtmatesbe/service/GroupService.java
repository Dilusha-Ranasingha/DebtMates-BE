package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.group.*;
import com.example.debtmatesbe.model.GroupDetails;
import com.example.debtmatesbe.model.GroupMembers;
import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.GroupDetailsRepository;
import com.example.debtmatesbe.repo.GroupMembersRepository;
import com.example.debtmatesbe.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {

    private final GroupDetailsRepository groupDetailsRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupDetailsRepository groupDetailsRepository,
                        GroupMembersRepository groupMembersRepository,
                        UserRepository userRepository) {
        this.groupDetailsRepository = groupDetailsRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.userRepository = userRepository;
    }

    public GroupResponse createGroup(CreateGroupRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User creator = userRepository.findByUsername(username);
        if (creator == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found");
        }

        GroupDetails group = new GroupDetails();
        group.setGroupName(request.getGroupName());
        group.setNumMembers(request.getNumMembers());
        group.setGroupDescription(request.getGroupDescription());
        group.setCreator(creator);
        GroupDetails savedGroup = groupDetailsRepository.save(group);

        // Add the creator to the group_members table
        GroupMembers creatorMembership = new GroupMembers();
        creatorMembership.setGroup(savedGroup);
        creatorMembership.setUser(creator);
        groupMembersRepository.save(creatorMembership);

        return mapToGroupResponse(savedGroup, true);
    }

    public GroupResponse updateGroup(Long groupId, UpdateGroupRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        GroupDetails group = groupDetailsRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can update the group");
        }

        group.setGroupName(request.getGroupName());
        group.setNumMembers(request.getNumMembers());
        group.setGroupDescription(request.getGroupDescription());
        GroupDetails updatedGroup = groupDetailsRepository.save(group);

        return mapToGroupResponse(updatedGroup, true);
    }

    public List<GroupResponse> getUserGroups() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Fetch groups where the user is a member
        List<GroupMembers> memberships = groupMembersRepository.findByUser(user);
        // Fetch groups where the user is the creator
        List<GroupDetails> createdGroups = groupDetailsRepository.findAll().stream()
                .filter(group -> group.getCreator().getId().equals(user.getId()))
                .toList(); // Updated to use toList()

        // Combine groups from memberships and created groups, avoiding duplicates
        Set<GroupDetails> allGroups = new HashSet<>();
        allGroups.addAll(memberships.stream().map(GroupMembers::getGroup).toList()); // Updated to use toList()
        allGroups.addAll(createdGroups);

        // Map to GroupResponse
        return allGroups.stream()
                .map(group -> mapToGroupResponse(group, group.getCreator().getId().equals(user.getId())))
                .toList(); // Updated to use toList()
    }

    public void addMembers(Long groupId, AddMembersRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        GroupDetails group = groupDetailsRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        if (!group.getCreator().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can add members");
        }

        List<User> members = userRepository.findAllById(request.getUserIds());
        if (members.size() != request.getUserIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some users not found");
        }

        if (members.size() > group.getNumMembers()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many members for this group");
        }

        for (User member : members) {
            GroupMembers groupMember = new GroupMembers();
            groupMember.setGroup(group);
            groupMember.setUser(member);
            groupMembersRepository.save(groupMember);
        }
    }

    public List<MemberResponse> getGroupMembers(Long groupId) {
        // Verify the group exists
        GroupDetails group = groupDetailsRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        // Fetch the members of the group
        List<GroupMembers> groupMembers = groupMembersRepository.findByGroupGroupId(groupId);

        // Map to MemberResponse DTO
        return groupMembers.stream()
                .map(groupMember -> {
                    MemberResponse response = new MemberResponse();
                    response.setId(groupMember.getUser().getId());
                    response.setUsername(groupMember.getUser().getUsername());
                    return response;
                })
                .toList();
    }

    // Helper method to map GroupDetails to GroupResponse
    private GroupResponse mapToGroupResponse(GroupDetails group, boolean isCreator) {
        GroupResponse response = new GroupResponse();
        response.setGroupId(group.getGroupId());
        response.setGroupName(group.getGroupName());
        response.setNumMembers(group.getNumMembers());
        response.setGroupDescription(group.getGroupDescription());
        response.setCreator(isCreator);
        return response;
    }
}