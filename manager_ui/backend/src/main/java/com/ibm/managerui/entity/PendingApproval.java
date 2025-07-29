package com.ibm.managerui.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String status; // Pending, Approved, Rejected
    private boolean updated = false; // true if the user has resubmitted/updated their form
    private String rejectionReason;
    private String functionalManagerEmail; // Store functional manager's email

    @Lob
    @Column(columnDefinition = "TEXT")
    private String profileData; // JSON string of all profile/skills data

    @Lob
    @Column(columnDefinition = "TEXT")
    private String previousProfileData; // JSON string of previous profile/skills data for comparison

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
} 