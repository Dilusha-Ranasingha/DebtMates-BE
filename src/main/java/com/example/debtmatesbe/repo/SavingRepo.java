package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.SavingsPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingRepo extends JpaRepository<SavingsPlan, Long> {
}