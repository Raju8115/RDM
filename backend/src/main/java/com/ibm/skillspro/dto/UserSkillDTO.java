package com.ibm.skillspro.dto;

public class UserSkillDTO {
    private Long id;
    private String practiceName;
    private String practiceAreaName;
    private String productName;
    private String projectsDone;
    private String selfAssessmentLevel;
    private String professionalLevel;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getPracticeAreaName() {
        return practiceAreaName;
    }

    public void setPracticeAreaName(String practiceAreaName) {
        this.practiceAreaName = practiceAreaName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProjectsDone() {
        return projectsDone;
    }

    public void setProjectsDone(String projectsDone) {
        this.projectsDone = projectsDone;
    }

    public String getSelfAssessmentLevel() {
        return selfAssessmentLevel;
    }

    public void setSelfAssessmentLevel(String selfAssessmentLevel) {
        this.selfAssessmentLevel = selfAssessmentLevel;
    }

    public String getProfessionalLevel() {
        return professionalLevel;
    }

    public void setProfessionalLevel(String professionalLevel) {
        this.professionalLevel = professionalLevel;
    }
} 