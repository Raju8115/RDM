package com.ibm.skillspro.dto;

public class UserAncillarySkillDTO {
    private String id;
    private String technology;
    private String product;
    private boolean certified;
    private String certificationLink;
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

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    public String getCertificationLink() {
        return certificationLink;
    }

    public void setCertificationLink(String certificationLink) {
        this.certificationLink = certificationLink;
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