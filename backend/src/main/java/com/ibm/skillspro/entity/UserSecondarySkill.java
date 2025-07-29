package com.ibm.skillspro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_secondary_skills")
public class UserSecondarySkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "practice")
    private String practice;

    @Column(name = "practice_area")
    private String practiceArea;

    @Column(name = "products_technologies")
    private String productsTechnologies;

    @Column(name = "duration")
    private String duration;

    @Column(name = "roles")
    private String roles;

    @Column(name = "certification_level", length = 100)
    private String certificationLevel;

    @Column(name = "recency_of_certification", length = 100)
    private String recencyOfCertification;

    @Column(name = "pending_delete", nullable = false)
    private boolean pendingDelete = false;

    @Transient
    private Double certificationScore;

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

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public String getPracticeArea() {
        return practiceArea;
    }

    public void setPracticeArea(String practiceArea) {
        this.practiceArea = practiceArea;
    }

    public String getProductsTechnologies() {
        return productsTechnologies;
    }

    public void setProductsTechnologies(String productsTechnologies) {
        this.productsTechnologies = productsTechnologies;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(String certificationLevel) {
        this.certificationLevel = certificationLevel;
    }

    public String getRecencyOfCertification() {
        return recencyOfCertification;
    }

    public void setRecencyOfCertification(String recencyOfCertification) {
        this.recencyOfCertification = recencyOfCertification;
    }

    public Double getCertificationScore() {
        return certificationScore;
    }

    public void setCertificationScore(Double certificationScore) {
        this.certificationScore = certificationScore;
    }

    public boolean isPendingDelete() {
        return pendingDelete;
    }

    public void setPendingDelete(boolean pendingDelete) {
        this.pendingDelete = pendingDelete;
    }

    public static double calculateCertificationScore(String certLevel, String recency) {
        double certLevelScore = switch (certLevel == null ? "" : certLevel.trim().toLowerCase()) {
            case "none" -> 0;
            case "foundation" -> 2;
            case "associate" -> 4;
            case "professional" -> 6;
            case "advanced" -> 8;
            case "expert / master" -> 10;
            default -> 0;
        };
        double recencyScore = switch (recency == null ? "" : recency.trim().toLowerCase()) {
            case "2 – 3 years" -> 2;
            case "1.5 – 2 years" -> 4;
            case "1 year" -> 6;
            case "<6 months" -> 8;
            case "<3 months" -> 10;
            default -> 0;
        };
        return (0.5 * certLevelScore) + (0.2 * recencyScore);
    }
}