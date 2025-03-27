package com.example.debtmatesbe.service;

import com.example.debtmatesbe.dto.SavingsPlanDTO;
import com.example.debtmatesbe.exception.ResourceNotFoundException;
import com.example.debtmatesbe.model.SavingsPlan;
import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.SavingRepo;
import com.example.debtmatesbe.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavingsPlanServiceImpl implements SavingsPlanService {

    private final SavingRepo savingsPlanRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public List<SavingsPlan> getAllPlansByUsername(String username) {
        User user = getUserByUsername(username);
        return savingsPlanRepository.findByUser(user);
    }

    @Override
    public SavingsPlan getPlanByIdAndUsername(Long id, String username) {
        User user = getUserByUsername(username);
        return savingsPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SavingsPlan", "id", String.valueOf(id)));
    }

    @Override
    @Transactional
    public SavingsPlan createPlan(SavingsPlanDTO planDTO, String username) {
        User user = getUserByUsername(username);

        LocalDate nextDepositDate = calculateNextDepositDate(planDTO.getStartDate(), planDTO.getDepositFrequency());

        SavingsPlan plan = SavingsPlan.builder()
                .title(planDTO.getTitle())
                .goalAmount(planDTO.getGoalAmount())
                .currentAmount(planDTO.getCurrentAmount())
                .startDate(planDTO.getStartDate())
                .endDate(planDTO.getEndDate())
                .nextDepositDate(nextDepositDate)
                .depositFrequency(planDTO.getDepositFrequency())
                .planType(planDTO.getPlanType())
                .status(SavingsPlan.PlanStatus.ACTIVE)
                .build();

        SavingsPlan savedPlan = savingsPlanRepository.save(plan);

        // Schedule initial notification
        notificationService.scheduleDepositReminder(savedPlan);

        return savedPlan;
    }

    @Override
    @Transactional
    public SavingsPlan updatePlan(Long id, SavingsPlanDTO planDTO, String username) {
        SavingsPlan existingPlan = getPlanByIdAndUsername(id, username);

        existingPlan.setTitle(planDTO.getTitle());
        existingPlan.setGoalAmount(planDTO.getGoalAmount());
        existingPlan.setCurrentAmount(planDTO.getCurrentAmount());
        existingPlan.setEndDate(planDTO.getEndDate());

        // Only update deposit frequency if it changed
        if (existingPlan.getDepositFrequency() != planDTO.getDepositFrequency()) {
            existingPlan.setDepositFrequency(planDTO.getDepositFrequency());
            existingPlan.setNextDepositDate(calculateNextDepositDate(LocalDate.now(), planDTO.getDepositFrequency()));

            // Update notification for new deposit date
            notificationService.cancelReminder(existingPlan.getId());
            notificationService.scheduleDepositReminder(existingPlan);
        }

        existingPlan.setPlanType(planDTO.getPlanType());

        return savingsPlanRepository.save(existingPlan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id, String username) {
        SavingsPlan plan = getPlanByIdAndUsername(id, username);

        // Cancel any scheduled notifications
        notificationService.cancelReminder(id);

        savingsPlanRepository.delete(plan);
    }

    @Override
    @Transactional
    public SavingsPlan recordDeposit(Long id, Double amount, String username) {
        SavingsPlan plan = getPlanByIdAndUsername(id, username);

        plan.setCurrentAmount(plan.getCurrentAmount() + amount);

        // Calculate next deposit date based on frequency
        plan.setNextDepositDate(calculateNextDepositDate(LocalDate.now(), plan.getDepositFrequency()));

        // Check if goal has been reached
        if (plan.getCurrentAmount() >= plan.getGoalAmount()) {
            plan.setStatus(SavingsPlan.PlanStatus.COMPLETED);
            notificationService.sendGoalAchievedNotification(plan);
            notificationService.cancelReminder(id);
        } else {
            // Schedule next reminder
            notificationService.cancelReminder(id);
            notificationService.scheduleDepositReminder(plan);
        }

        return savingsPlanRepository.save(plan);
    }

    @Override
    @Transactional
    public SavingsPlan completePlan(Long id, String username) {
        SavingsPlan plan = getPlanByIdAndUsername(id, username);

        plan.setStatus(SavingsPlan.PlanStatus.COMPLETED);

        // Cancel any scheduled notifications
        notificationService.cancelReminder(id);

        return savingsPlanRepository.save(plan);
    }

    @Override
    @Scheduled(cron = "0 0 8 * * ?") // Run every day at 8 AM
    @Transactional
    public void processDepositReminders() {
        LocalDate today = LocalDate.now();
        List<SavingsPlan> duePlans = savingsPlanRepository.findPlansWithDueDeposits(today);

        for (SavingsPlan plan : duePlans) {
            notificationService.sendDepositReminderNotification(plan);
        }
    }

    private User getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    private LocalDate calculateNextDepositDate(LocalDate from, SavingsPlan.DepositFrequency frequency) {
        LocalDate nextDate;
        switch (frequency) {
            case DAILY:
                nextDate = from.plusDays(1);
                break;
            case WEEKLY:
                nextDate = from.plusWeeks(1);
                break;
            case BIWEEKLY:
                nextDate = from.plusWeeks(2);
                break;
            case MONTHLY:
                nextDate = from.plusMonths(1);
                break;
            default:
                nextDate = from.plusMonths(1);
        }
        return nextDate;
    }
}
