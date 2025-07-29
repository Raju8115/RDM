package com.ibm.skillspro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "professional_certifications")
public class ProfessionalCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title")
    private String title;

    @Column(name = "certified")
    private boolean certified;

    @Column(name = "certification_link")
    private String certificationLink;

    @Column(name = "certification_level", length = 50)
    private String certificationLevel;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public static double calculateCertificationScore(String certLevel) {
        double certLevelScore = switch (certLevel == null ? "" : certLevel.trim().toLowerCase()) {
            case "foundation" -> 4;
            case "experienced" -> 6;
            case "expert" -> 8;
            case "thought leader" -> 10;
            default -> 0;
        };
        return 0.5 * certLevelScore;
    }
}