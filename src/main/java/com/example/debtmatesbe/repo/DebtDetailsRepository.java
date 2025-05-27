package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.DebtDetails;
import com.example.debtmatesbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtDetailsRepository extends JpaRepository<DebtDetails, Long> {
    List<DebtDetails> findByGroupGroupId(Long groupId);
    List<DebtDetails> findByMember(User member);
}