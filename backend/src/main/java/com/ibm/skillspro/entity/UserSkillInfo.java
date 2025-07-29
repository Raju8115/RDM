package com.ibm.skillspro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_skill_info")
public class UserSkillInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_skill_id", nullable = false)
    private Long userSkillId;

    @Column(name = "project_title", length = 512)
    private String projectTitle;

    @Column(name = "technologies_used", length = 512)
    private String technologiesUsed;

    @Column(name = "duration", length = 50)
    private String duration;

    @Column(name = "responsibilities", length = 1024)
    private String responsibilities;

    @Column(name = "client_tier", length = 50)
    private String clientTier;

    @Column(name = "client_tier_v2", length = 50)
    private String clientTierV2;

    @Column(name = "project_complexity", length = 50)
    private String projectComplexity;

    @Column(name = "pending_delete", nullable = false)
    private boolean pendingDelete = false;

    @Transient
    private Double projectScore;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserSkillId() {
        return userSkillId;
    }

    public void setUserSkillId(Long userSkillId) {
        this.userSkillId = userSkillId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getTechnologiesUsed() {
        return technologiesUsed;
    }

    public void setTechnologiesUsed(String technologiesUsed) {
        this.technologiesUsed = technologiesUsed;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getClientTier() {
        return clientTier;
    }

    public void setClientTier(String clientTier) {
        this.clientTier = clientTier;
    }

    public String getClientTierV2() {
        return clientTierV2;
    }

    public void setClientTierV2(String clientTierV2) {
        this.clientTierV2 = clientTierV2;
    }

    public String getProjectComplexity() {
        return projectComplexity;
    }

    public void setProjectComplexity(String projectComplexity) {
        this.projectComplexity = projectComplexity;
    }

    public Double getProjectScore() {
        return projectScore;
    }

    public void setProjectScore(Double projectScore) {
        this.projectScore = projectScore;
    }

    public boolean isPendingDelete() {
        return pendingDelete;
    }

    public void setPendingDelete(boolean pendingDelete) {
        this.pendingDelete = pendingDelete;
    }

    public static double calculateProjectScore(String complexity, String responsibilities, String duration,
            String clientTier) {
        double complexityScore = switch (complexity == null ? "" : complexity.trim().toLowerCase()) {
            case "simple" -> 2;
            case "moderate" -> 4;
            case "advanced" -> 6;
            case "complex" -> 8;
            case "highly complex" -> 10;
            default -> 0;
        };
        double roleScore = switch (responsibilities == null ? "" : responsibilities.trim().toLowerCase()) {
            case "shadow – no client interaction/no deliverables" -> 1;
            case "delivery team member, limited client facing" -> 2;
            case "active contributor to client deliverables, supports meetings" -> 4;
            case "owns specific module(s), regular client engagements" -> 6;
            case "drives delivery, owns client discussions" -> 8;
            case "engagement lead, owns delivery and is the face of tel for client" -> 10;
            default -> 0;
        };
        double durationScore = switch (duration == null ? "" : duration.trim().toLowerCase()) {
            case "2 – 3 years" -> 2;
            case "1.5 – 2 years" -> 4;
            case "1 year" -> 6;
            case "<6 months" -> 8;
            case "<3 months" -> 10;
            default -> 0;
        };
        double clientTierScore = switch (clientTier == null ? "" : clientTier.trim().toLowerCase()) {
            case "internal" -> 2;
            case "midsized" -> 4;
            case "strategic" -> 6;
            case "fortune 500/100" -> 8;
            case "global/federal/regulated" -> 10;
            default -> 0;
        };
        return (0.35 * complexityScore) + (0.35 * roleScore) + (0.15 * durationScore) + (0.15 * clientTierScore);
    }
}