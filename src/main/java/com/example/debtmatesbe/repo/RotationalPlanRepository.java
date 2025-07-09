package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.RotationalGroup;
import com.example.debtmatesbe.model.RotationalPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RotationalPlanRepository extends JpaRepository<RotationalPlan, Long> {
    List<RotationalPlan> findByGroup(RotationalGroup group);
}