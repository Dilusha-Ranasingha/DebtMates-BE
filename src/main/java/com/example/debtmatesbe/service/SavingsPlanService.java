package com.example.debtmatesbe.service;

import com.example.debtmatesbe.model.SavingsPlan;
import com.example.debtmatesbe.repo.SavingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavingsPlanService {
    private static final Logger logger = LoggerFactory.getLogger(SavingsPlanService.class);

    @Autowired
    private SavingRepo repository;

    public List<SavingsPlan> getAllPlans() {
        return repository.findAll();
    }

    public Optional<SavingsPlan> getPlanById(Long id) {
        return repository.findById(id);
    }

    public SavingsPlan createPlan(SavingsPlan plan) {
        logger.info("Creating plan: {}", plan);
        SavingsPlan savedPlan = repository.save(plan);
        logger.info("Saved plan: {}", savedPlan);
        return savedPlan;
    }

    public SavingsPlan updatePlan(Long id, SavingsPlan planDetails) {
        SavingsPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setPlanName(planDetails.getPlanName());
        plan.setPlanType(planDetails.getPlanType());
        plan.setGoalAmount(planDetails.getGoalAmount());
        plan.setCurrentAmount(planDetails.getCurrentAmount());
        plan.setStartDate(planDetails.getStartDate());
        plan.setEndDate(planDetails.getEndDate());
        plan.setInitialDeposit(planDetails.getInitialDeposit());
        plan.setFrequency(planDetails.getFrequency());

        return repository.save(plan);
    }

    public void deletePlan(Long id) {
        SavingsPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        repository.delete(plan);
    }

    public SavingsPlan recordDeposit(Long id, double amount) {
        SavingsPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        plan.setCurrentAmount(plan.getCurrentAmount() + amount);
        return repository.save(plan);
    }
}