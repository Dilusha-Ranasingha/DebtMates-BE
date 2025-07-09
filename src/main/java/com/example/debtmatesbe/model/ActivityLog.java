package com.example.debtmatesbe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Data
@NoArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private String username;
    private String email;
    private String role;
    private LocalDateTime timestamp;

    public enum ActivityType {
        REGISTRATION, LOGIN
    }

    public ActivityLog(ActivityType activityType, String username, String email, String role) {
        this.activityType = activityType;
        this.username = username;
        this.email = email;
        this.role = role;
        this.timestamp = LocalDateTime.now();
    }
}