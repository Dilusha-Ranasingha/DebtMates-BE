package com.example.debtmatesbe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_details")
@Data
@NoArgsConstructor
public class GroupDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    private String groupName;

    private int numMembers;

    private String groupDescription;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
}

//This entity maps to the GroupDetailsTable. It includes a creator field to track the
// user who created the group, linked to the User entity via a foreign key.