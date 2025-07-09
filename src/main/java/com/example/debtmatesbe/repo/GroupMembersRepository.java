package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.GroupMembers;
import com.example.debtmatesbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {
    List<GroupMembers> findByUser(User user);
    List<GroupMembers> findByGroupGroupId(Long groupId);
}



//This repository provides CRUD operations for the GroupMembers entity and includes
//custom queries to find group members by user or group.