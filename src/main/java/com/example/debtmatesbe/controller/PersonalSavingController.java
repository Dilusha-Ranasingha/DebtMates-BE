package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.dto.SavingsPlanDTO;
import com.example.debtmatesbe.model.SavingsPlan;
import com.example.debtmatesbe.service.SavingsPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PersonalSavingController {

    private final SavingsPlanService savingsPlanService;

    @GetMapping
    public ResponseEntity<List<SavingsPlan>> getAllPlans(Authentication authentication) {
        String username = authentication.getName();
        List<SavingsPlan> plans = savingsPlanService.getAllPlansByUsername(username);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsPlan> getPlanById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        SavingsPlan plan = savingsPlanService.getPlanByIdAndUsername(id, username);
        return ResponseEntity.ok(plan);
    }

    @PostMapping
    public ResponseEntity<SavingsPlan> createPlan(@Valid @RequestBody SavingsPlanDTO planDTO, Authentication authentication) {
        String username = authentication.getName();
        SavingsPlan createdPlan = savingsPlanService.createPlan(planDTO, username);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsPlan> updatePlan(@PathVariable Long id, @Valid @RequestBody SavingsPlanDTO planDTO, Authentication authentication) {
        String username = authentication.getName();
        SavingsPlan updatedPlan = savingsPlanService.updatePlan(id, planDTO, username);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        savingsPlanService.deletePlan(id, username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<SavingsPlan> recordDeposit(@PathVariable Long id, @RequestParam Double amount, Authentication authentication) {
        String username = authentication.getName();
        SavingsPlan updatedPlan = savingsPlanService.recordDeposit(id, amount, username);
        return ResponseEntity.ok(updatedPlan);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<SavingsPlan> completePlan(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        SavingsPlan completedPlan = savingsPlanService.completePlan(id, username);
        return ResponseEntity.ok(completedPlan);
    }
}


