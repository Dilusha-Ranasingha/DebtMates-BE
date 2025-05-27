package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.RotationalGroup;
import com.example.debtmatesbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RotationalGroupRepository extends JpaRepository<RotationalGroup, Long> {
    List<RotationalGroup> findByCreator(User creator);
    List<RotationalGroup> findByMembersContaining(User member);
}