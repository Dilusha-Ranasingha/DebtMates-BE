package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.group.AddMembersRequest;
import com.example.debtmatesbe.dto.group.CreateGroupRequest;
import com.example.debtmatesbe.dto.group.GroupResponse;
import com.example.debtmatesbe.dto.group.UpdateGroupRequest;
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

import java.util.List;
import java.util.stream.Collectors;

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

        GroupResponse response = new GroupResponse();
        response.setGroupId(savedGroup.getGroupId());
        response.setGroupName(savedGroup.getGroupName());
        response.setNumMembers(savedGroup.getNumMembers());
        response.setGroupDescription(savedGroup.getGroupDescription());
        response.setCreator(true);
        return response;
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

        GroupResponse response = new GroupResponse();
        response.setGroupId(updatedGroup.getGroupId());
        response.setGroupName(updatedGroup.getGroupName());
        response.setNumMembers(updatedGroup.getNumMembers());
        response.setGroupDescription(updatedGroup.getGroupDescription());
        response.setCreator(true);
        return response;
    }

    public List<GroupResponse> getUserGroups() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<GroupMembers> memberships = groupMembersRepository.findByUser(user);
        List<GroupDetails> createdGroups = groupDetailsRepository.findAll().stream()
                .filter(group -> group.getCreator().getId().equals(user.getId()))
                .collect(Collectors.toList());

        return memberships.stream()
                .map(membership -> {
                    GroupDetails group = membership.getGroup();
                    GroupResponse response = new GroupResponse();
                    response.setGroupId(group.getGroupId());
                    response.setGroupName(group.getGroupName());
                    response.setNumMembers(group.getNumMembers());
                    response.setGroupDescription(group.getGroupDescription());
                    response.setCreator(group.getCreator().getId().equals(user.getId()));
                    return response;
                })
                .collect(Collectors.toList());
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
}




/*This service contains the business logic for group management:
    createGroup: Creates a new group and sets the logged-in user as the creator.
    updateGroup: Updates a group, but only if the logged-in user is the creator.
    getUserGroups: Retrieves all groups the logged-in user is part of (as a member or creator).
    addMembers: Adds members to a group, ensuring the logged-in user is the creator and the
                number of members doesn’t exceed the group’s limit.
*/