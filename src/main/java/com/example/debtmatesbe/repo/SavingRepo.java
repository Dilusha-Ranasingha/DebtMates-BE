package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.SavingsPlan;
import com.example.debtmatesbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingRepo extends JpaRepository<SavingsPlan, Long> {
    List<SavingsPlan> findByUser(User user);
    Optional<SavingsPlan> findByIdAndUser(Long id, User user);

    @Query("SELECT p FROM SavingsPlan p WHERE p.nextDepositDate <= :today AND p.status = 'ACTIVE'")
    List<SavingsPlan> findPlansWithDueDeposits(LocalDate today);
}