package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.model.SavingsPlan;
import com.example.debtmatesbe.service.SavingsPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
@CrossOrigin(origins = "http://http://localhost:5173") // Adjust based on your frontend URL
public class SavingsPlanController {

    @Autowired
    private SavingsPlanService service;

    @GetMapping("/plans")
    public ResponseEntity<List<SavingsPlan>> getAllPlans() {
        return ResponseEntity.ok(service.getAllPlans());
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<SavingsPlan> getPlanById(@PathVariable Long id) {
        return service.getPlanById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/plans")
    public ResponseEntity<SavingsPlan> createPlan(@RequestBody SavingsPlan plan) {
        return ResponseEntity.ok(service.createPlan(plan));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<SavingsPlan> updatePlan(@PathVariable Long id, @RequestBody SavingsPlan plan) {
        return ResponseEntity.ok(service.updatePlan(id, plan));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        service.deletePlan(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/plans/{id}/deposit")
    public ResponseEntity<SavingsPlan> recordDeposit(@PathVariable Long id, @RequestBody DepositRequest deposit) {
        return ResponseEntity.ok(service.recordDeposit(id, deposit.getAmount()));
    }
}


