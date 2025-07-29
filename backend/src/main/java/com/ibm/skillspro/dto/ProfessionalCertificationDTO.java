package com.ibm.skillspro.dto;

public class ProfessionalCertificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private Boolean certified;
    private String certificationLink;
    private String certificationLevel;
    private Double certificationScore;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCertified() {
        return certified;
    }

    public void setCertified(Boolean certified) {
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

    public Double getCertificationScore() {
        return certificationScore;
    }

    public void setCertificationScore(Double certificationScore) {
        this.certificationScore = certificationScore;
    }
}