package com.example.debtmatesbe.service;

import com.example.debtmatesbe.model.SavingsPlan;

public interface NotificationService {
    void scheduleDepositReminder(SavingsPlan plan);
    void cancelReminder(Long planId);
    void sendDepositReminderNotification(SavingsPlan plan);
    void sendGoalAchievedNotification(SavingsPlan plan);
}
