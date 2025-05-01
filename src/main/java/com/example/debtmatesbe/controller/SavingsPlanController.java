package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.dto.personalsaving.CreateSavingsPlanDto;
import com.example.debtmatesbe.dto.personalsaving.DepositDto;
import com.example.debtmatesbe.dto.personalsaving.SavingsPlanDto;
import com.example.debtmatesbe.dto.personalsaving.UpdateSavingsPlanDto;
import com.example.debtmatesbe.exception.ResourceNotFoundException;
import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.service.SavingsPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/savings-plans")
public class SavingsPlanController {

    private final SavingsPlanService savingsPlanService;

    public SavingsPlanController(SavingsPlanService savingsPlanService) {
        this.savingsPlanService = savingsPlanService;
    }

    @PostMapping
    public ResponseEntity<SavingsPlanDto> createPlan(@Valid @RequestBody CreateSavingsPlanDto createDto) {
        SavingsPlanDto createdPlan = savingsPlanService.createPlan(createDto);
        return ResponseEntity.status(201).body(createdPlan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsPlanDto> getPlanById(@PathVariable Long id) {
        SavingsPlanDto plan = savingsPlanService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavingsPlanDto>> getPlansByUserId(@PathVariable Long userId) {
        try {
            List<SavingsPlanDto> plans = savingsPlanService.getPlansByUserId(userId);
            return ResponseEntity.ok(plans);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsPlanDto> updatePlan(@PathVariable Long id, @Valid @RequestBody UpdateSavingsPlanDto updateDto) {
        SavingsPlanDto updatedPlan = savingsPlanService.updatePlan(id, updateDto);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        savingsPlanService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<SavingsPlanDto> recordDeposit(@PathVariable Long id, @Valid @RequestBody DepositDto depositDto) {
        SavingsPlanDto updatedPlan = savingsPlanService.recordDeposit(id, depositDto);
        return ResponseEntity.ok(updatedPlan);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, BigDecimal>> getSavingStats(@AuthenticationPrincipal User user) {
        List<SavingsPlanDto> plans = savingsPlanService.getPlansByUserId(user.getId());
        BigDecimal totalSaved = plans.stream()
                .map(SavingsPlanDto::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGoal = plans.stream()
                .map(SavingsPlanDto::getGoalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> stats = new HashMap<>();
        stats.put("totalSaved", totalSaved);
        stats.put("totalGoal", totalGoal);
        return ResponseEntity.ok(stats);
    }
}


