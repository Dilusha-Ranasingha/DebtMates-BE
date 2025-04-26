package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.CreateSavingsPlanDto;
import com.example.debtmatesbe.dto.DepositDto;
import com.example.debtmatesbe.dto.SavingsPlanDto;
import com.example.debtmatesbe.dto.UpdateSavingsPlanDto;

import java.util.List;

public interface SavingsPlanService {
    SavingsPlanDto createPlan(CreateSavingsPlanDto createDto);
    SavingsPlanDto getPlanById(Long id);
    List<SavingsPlanDto> getPlansByUserId(Long userId);
    SavingsPlanDto updatePlan(Long id, UpdateSavingsPlanDto updateDto);
    void deletePlan(Long id);
    SavingsPlanDto recordDeposit(Long id, DepositDto depositDto);
}