package com.example.debtmatesbe.service;

import com.example.debtmatesbe.model.SavingsPlan;
import com.example.debtmatesbe.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    // In a real application, this would be a proper scheduled task service
    private final Map<Long, Runnable> scheduledReminders = new ConcurrentHashMap<>();

    @Override
    public void scheduleDepositReminder(SavingsPlan plan) {
        // In a real app, this would use a proper scheduling mechanism
        // For demo, we just log and store the reminder task
        Runnable reminderTask = () -> sendDepositReminderNotification(plan);
        scheduledReminders.put(plan.getId(), reminderTask);

        logger.info("Scheduled reminder for plan: {} on date: {}",
                plan.getTitle(), plan.getNextDepositDate());
    }

    @Override
    public void cancelReminder(Long planId) {
        scheduledReminders.remove(planId);
        logger.info("Cancelled reminder for plan ID: {}", planId);
    }

    @Override
    public void sendDepositReminderNotification(SavingsPlan plan) {
        User user = plan.getUser();

        String message = String.format(
                "Reminder: Your next deposit for '%s' is due today. Current progress: %d%%",
                plan.getTitle(),
                plan.getProgressPercentage()
        );

        // In a real application, this would send an email, push notification, etc.
        logger.info("Notification to user: {} - {}", user.getUsername(), message);
    }

    @Override
    public void sendGoalAchievedNotification(SavingsPlan plan) {
        User user = plan.getUser();

        String message = String.format(
                "Congratulations! You've reached your savings goal of %s for '%s'!",
                formatCurrency(plan.getGoalAmount()),
                plan.getTitle()
        );

        // In a real application, this would send an email, push notification, etc.
        logger.info("Notification to user: {} - {}", user.getUsername(), message);
    }

    private String formatCurrency(double amount) {
        return String.format("$%,.2f", amount);
    }
}
