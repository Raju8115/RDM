package com.ibm.skillspro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_skill")
public class UserSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "practice_id")
    private Long practiceId;

    @Column(name = "practice_area_id", nullable = false)
    private Long practiceAreaId;

    @Column(name = "practice_product_technology_id")
    private Long practiceProductTechnologyId;

    @Column(name = "projects_done", length = 50)
    private String projectsDone;

    @Column(name = "self_assessment_level", length = 1024)
    private String selfAssessmentLevel;

    @Column(name = "professional_level", length = 1024)
    private String professionalLevel;

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

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public Long getPracticeAreaId() {
        return practiceAreaId;
    }

    public void setPracticeAreaId(Long practiceAreaId) {
        this.practiceAreaId = practiceAreaId;
    }

    public Long getPracticeProductTechnologyId() {
        return practiceProductTechnologyId;
    }

    public void setPracticeProductTechnologyId(Long practiceProductTechnologyId) {
        this.practiceProductTechnologyId = practiceProductTechnologyId;
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