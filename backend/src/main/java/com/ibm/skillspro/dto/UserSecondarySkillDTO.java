package com.ibm.skillspro.dto;

public class UserSecondarySkillDTO {
    private String id;
    private String practice;
    private String practiceArea;
    private String productsTechnologies;
    private String duration;
    private String roles;
    private String certificationLevel;
    private String recencyOfCertification;
    private Double certificationScore;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}