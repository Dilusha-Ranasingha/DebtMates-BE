package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.personalsaving.CreateSavingsPlanDto;
import com.example.debtmatesbe.dto.personalsaving.DepositDto;
import com.example.debtmatesbe.dto.personalsaving.SavingsPlanDto;
import com.example.debtmatesbe.dto.personalsaving.UpdateSavingsPlanDto;

import java.util.List;

public interface SavingsPlanService {
    SavingsPlanDto createPlan(CreateSavingsPlanDto createDto);
    SavingsPlanDto getPlanById(Long id);
    List<SavingsPlanDto> getPlansByUserId(Long userId);
    SavingsPlanDto updatePlan(Long id, UpdateSavingsPlanDto updateDto);
    void deletePlan(Long id);
    SavingsPlanDto recordDeposit(Long id, DepositDto depositDto);
}
