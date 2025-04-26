package com.example.debtmatesbe.service.impl;

import com.example.debtmatesbe.dto.CreateSavingsPlanDto;
import com.example.debtmatesbe.dto.DepositDto;
import com.example.debtmatesbe.dto.SavingsPlanDto;
import com.example.debtmatesbe.dto.UpdateSavingsPlanDto;
import com.example.debtmatesbe.model.SavingsPlan;
import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.exception.ResourceNotFoundException;
import com.example.debtmatesbe.repo.SavingRepo;
import com.example.debtmatesbe.repo.UserRepository;
import com.example.debtmatesbe.service.SavingsPlanService;
import com.example.debtmatesbe.util.Constants;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsPlanServiceImpl implements SavingsPlanService {

    private final SavingRepo savingsPlanRepository;
    private final UserRepository userRepository;

    public SavingsPlanServiceImpl(SavingRepo savingsPlanRepository, UserRepository userRepository) {
        this.savingsPlanRepository = savingsPlanRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SavingsPlanDto createPlan(CreateSavingsPlanDto createDto) {
        validateCreateDto(createDto);
        User user = userRepository.findById(createDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDto.getUserId()));

        SavingsPlan plan = new SavingsPlan();
        plan.setPlanName(createDto.getPlanName());
        plan.setPlanType(createDto.getPlanType());
        plan.setGoalAmount(createDto.getGoalAmount());
        plan.setCurrentAmount(createDto.getInitialDeposit());
        plan.setStartDate(createDto.getStartDate());
        plan.setEndDate(createDto.getEndDate());
        plan.setDepositFrequency(createDto.getDepositFrequency());
        plan.setNextDepositDate(calculateNextDepositDate(createDto.getStartDate(), createDto.getDepositFrequency()));
        plan.setUser(user);

        SavingsPlan savedPlan = savingsPlanRepository.save(plan);
        return mapToDto(savedPlan);
    }

    @Override
    public SavingsPlanDto getPlanById(Long id) {
        SavingsPlan plan = savingsPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings plan not found with id: " + id));
        return mapToDto(plan);
    }

    @Override
    public List<SavingsPlanDto> getPlansByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return savingsPlanRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SavingsPlanDto updatePlan(Long id, UpdateSavingsPlanDto updateDto) {
        validateUpdateDto(updateDto);
        SavingsPlan plan = savingsPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings plan not found with id: " + id));

        plan.setPlanName(updateDto.getTitle());
        plan.setGoalAmount(updateDto.getGoalAmount());
        plan.setCurrentAmount(updateDto.getCurrentAmount());
        plan.setEndDate(updateDto.getEndDate());
        plan.setDepositFrequency(updateDto.getDepositFrequency());
        plan.setNextDepositDate(calculateNextDepositDate(LocalDate.now(), updateDto.getDepositFrequency()));

        SavingsPlan updatedPlan = savingsPlanRepository.save(plan);
        return mapToDto(updatedPlan);
    }

    @Override
    public void deletePlan(Long id) {
        SavingsPlan plan = savingsPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings plan not found with id: " + id));
        savingsPlanRepository.delete(plan);
    }

    @Override
    public SavingsPlanDto recordDeposit(Long id, DepositDto depositDto) {
        SavingsPlan plan = savingsPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings plan not found with id: " + id));
        plan.setCurrentAmount(plan.getCurrentAmount().add(depositDto.getAmount()));
        plan.setNextDepositDate(calculateNextDepositDate(LocalDate.now(), plan.getDepositFrequency()));
        SavingsPlan updatedPlan = savingsPlanRepository.save(plan);
        return mapToDto(updatedPlan);
    }

    private void validateCreateDto(CreateSavingsPlanDto dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (dto.getInitialDeposit().compareTo(dto.getGoalAmount()) > 0) {
            throw new IllegalArgumentException("Initial deposit cannot exceed goal amount");
        }
        if (!Arrays.asList(Constants.VALID_PLAN_TYPES).contains(dto.getPlanType().toLowerCase())) {
            throw new IllegalArgumentException("Invalid plan type");
        }
        if (!Arrays.asList(Constants.VALID_FREQUENCIES).contains(dto.getDepositFrequency().toLowerCase())) {
            throw new IllegalArgumentException("Invalid deposit frequency");
        }
    }

    private void validateUpdateDto(UpdateSavingsPlanDto dto) {
        if (dto.getCurrentAmount().compareTo(dto.getGoalAmount()) > 0) {
            throw new IllegalArgumentException("Current amount cannot exceed goal amount");
        }
        if (!Arrays.asList(Constants.VALID_FREQUENCIES).contains(dto.getDepositFrequency().toLowerCase())) {
            throw new IllegalArgumentException("Invalid deposit frequency");
        }
    }

    private LocalDate calculateNextDepositDate(LocalDate currentDate, String frequency) {
        return switch (frequency.toLowerCase()) {
            case "daily" -> currentDate.plusDays(1);
            case "weekly" -> currentDate.plusWeeks(1);
            case "biweekly" -> currentDate.plusWeeks(2);
            case "monthly" -> currentDate.plusMonths(1);
            default -> currentDate.plusMonths(1); // Default to monthly
        };
    }

    private SavingsPlanDto mapToDto(SavingsPlan plan) {
        SavingsPlanDto dto = new SavingsPlanDto();
        dto.setId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setGoalAmount(plan.getGoalAmount());
        dto.setCurrentAmount(plan.getCurrentAmount());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setNextDepositDate(plan.getNextDepositDate());
        dto.setDepositFrequency(plan.getDepositFrequency());
        dto.setPlanType(plan.getPlanType());
        dto.setUserId(plan.getUser().getId());
        return dto;
    }
}
