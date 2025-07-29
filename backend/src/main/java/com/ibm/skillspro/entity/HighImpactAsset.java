package com.ibm.skillspro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "high_impact_assets")
public class HighImpactAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "title")
    private String title;

    @Column(name = "business_impact")
    private String businessImpact;

    @Column(name = "visibility_adoption")
    private String visibilityAdoption;

    @Column(name = "description")
    private String description;

    @Transient
    private Double impactScore;

    @Column(name = "pending_delete", nullable = false)
    private boolean pendingDelete = false;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBusinessImpact() {
        return businessImpact;
    }

    public void setBusinessImpact(String businessImpact) {
        this.businessImpact = businessImpact;
    }

    public String getVisibilityAdoption() {
        return visibilityAdoption;
    }

    public void setVisibilityAdoption(String visibilityAdoption) {
        this.visibilityAdoption = visibilityAdoption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(Double impactScore) {
        this.impactScore = impactScore;
    }

    public boolean isPendingDelete() {
        return pendingDelete;
    }

    public void setPendingDelete(boolean pendingDelete) {
        this.pendingDelete = pendingDelete;
    }

    public static double calculateImpactScore(String businessImpact, String visibilityAdoption) {
        double businessImpactScore = switch (businessImpact == null ? "" : businessImpact.trim().toLowerCase()) {
            case "no impact" -> 0;
            case "local utility (project specific and not much scope for reuse)" -> 2;
            case "productivity enhancer" -> 4;
            case "department wide efficiency" -> 6;
            case "major $/time savings" -> 8;
            case "strategic transformation / foak" -> 10;
            default -> 0;
        };
        double visibilityScore = switch (visibilityAdoption == null ? "" : visibilityAdoption.trim().toLowerCase()) {
            case "internal (not client shareable)" -> 2;
            case "2 to 4 client deployments" -> 4;
            case "5 to 8 client deployments" -> 6;
            case "9+ client deployments" -> 8;
            case "primary deal win driver" -> 10;
            default -> 0;
        };
        return 0.5 * businessImpactScore + 0.5 * visibilityScore;
    }
}