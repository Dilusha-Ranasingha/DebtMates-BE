package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.SavingsPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingRepo extends JpaRepository<SavingsPlan, Long> {
    List<SavingsPlan> findByUserId(Long userId);
}