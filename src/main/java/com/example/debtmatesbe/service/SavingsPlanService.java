package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.SavingsPlanDTO;
import com.example.debtmatesbe.model.SavingsPlan;

import java.util.List;

public interface SavingsPlanService {
    List<SavingsPlan> getAllPlansByUsername(String username);
    SavingsPlan getPlanByIdAndUsername(Long id, String username);
    SavingsPlan createPlan(SavingsPlanDTO planDTO, String username);
    SavingsPlan updatePlan(Long id, SavingsPlanDTO planDTO, String username);
    void deletePlan(Long id, String username);
    SavingsPlan recordDeposit(Long id, Double amount, String username);
    SavingsPlan completePlan(Long id, String username);
    void processDepositReminders();
}