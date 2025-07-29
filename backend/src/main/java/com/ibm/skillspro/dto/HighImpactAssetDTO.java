package com.ibm.skillspro.dto;

public class HighImpactAssetDTO {
    private Integer id;
    private Integer userId;
    private String title;
    private String businessImpact;
    private String visibilityAdoption;
    private String description;
    private Double impactScore;
    private boolean pendingDelete;

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
} 