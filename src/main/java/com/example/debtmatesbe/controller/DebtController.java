package com.example.debtmatesbe.controller;

import com.example.debtmatesbe.dto.debt.DebtResponse;
import com.example.debtmatesbe.dto.debt.RecordDebtRequest;
import com.example.debtmatesbe.service.DebtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DebtController {

    private final DebtService debtService;

    @Autowired
    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @PostMapping("/groups/{groupId}/debts")
    public ResponseEntity<Void> recordDebt(@PathVariable Long groupId,
                                           @Valid @RequestBody RecordDebtRequest request) {
        debtService.recordDebt(groupId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups/{groupId}/debts")
    public ResponseEntity<List<DebtResponse>> getGroupDebts(@PathVariable Long groupId) {
        List<DebtResponse> debts = debtService.getGroupDebts(groupId);
        return ResponseEntity.ok(debts);
    }

    @GetMapping("/users/me/debts")
    public ResponseEntity<List<DebtResponse>> getUserDebts() {
        List<DebtResponse> debts = debtService.getUserDebts();
        return ResponseEntity.ok(debts);
    }
}