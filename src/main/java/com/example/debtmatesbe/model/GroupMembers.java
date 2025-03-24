package com.example.debtmatesbe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_members")
@Data
@NoArgsConstructor
public class GroupMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupDetails group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

//This entity represents the junction table for the many-to-many relationship
// between GroupDetails and User. It links a group to its members.