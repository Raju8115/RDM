package com.ibm.skillspro.dto;

public class UserSkillInfoDTO {
    private String id;
    private String projectTitle;
    private String technologiesUsed;
    private String duration;
    private String responsibilities;
    private String clientTier;
    private String clientTierV2;
    private String projectComplexity;
    private Double projectScore;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}