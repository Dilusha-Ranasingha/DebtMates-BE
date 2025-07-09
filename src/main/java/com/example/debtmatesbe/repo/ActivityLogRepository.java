package com.example.debtmatesbe.repo;

import com.example.debtmatesbe.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByActivityType(ActivityLog.ActivityType activityType, Pageable pageable);

    Page<ActivityLog> findByActivityTypeAndRole(ActivityLog.ActivityType activityType, String role, Pageable pageable);
}