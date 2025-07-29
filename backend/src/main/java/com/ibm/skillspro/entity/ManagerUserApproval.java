package com.ibm.skillspro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager_user_approval")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerUserApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manager_email", nullable = false)
    private String managerEmail;

    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "status")
    private String status; // Pending, Approved, Rejected

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_updated")
    private boolean updated = false; // true if user resubmitted after rejection

    @Lob
    @Column(columnDefinition = "TEXT")
    private String profileData; // JSON string of all profile/skills data

    @Lob
    @Column(columnDefinition = "TEXT")
    private String previousProfileData; // Previous profileData for diffing

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 