package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.RotationalPayment;
import com.example.debtmatesbe.model.RotationalPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RotationalPaymentRepository extends JpaRepository<RotationalPayment, Long> {
    List<RotationalPayment> findByPlan(RotationalPlan plan);
}