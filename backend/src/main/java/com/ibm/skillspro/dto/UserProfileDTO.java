package com.ibm.skillspro.dto;

import java.util.List;
import com.ibm.skillspro.dto.UserCredentialDTO;
import com.ibm.skillspro.dto.ProfessionalCertificationDTO;
import com.ibm.skillspro.dto.HighImpactAssetDTO;

public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String slackId;
    private Long practiceId;
    private Long practiceAreaId;
    private Long practiceProductTechnologyId;
    private String projectsDone;
    private String selfAssessmentLevel;
    private String professionalLevel;
    private List<UserSkillDTO> primarySkills;
    private List<UserSecondarySkillDTO> secondarySkills;
    private List<UserAncillarySkillDTO> ancillarySkills;
    private List<UserSkillInfoDTO> projectExperiences;
    private boolean isNewUser;
    private List<UserCredentialDTO> badges;
    private List<ProfessionalCertificationDTO> professionalCertifications;
    private List<HighImpactAssetDTO> highImpactAssets;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSlackId() {
        return slackId;
    }

    public void setSlackId(String slackId) {
        this.slackId = slackId;
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

    public List<UserSkillDTO> getPrimarySkills() {
        return primarySkills;
    }

    public void setPrimarySkills(List<UserSkillDTO> primarySkills) {
        this.primarySkills = primarySkills;
    }

    public List<UserSecondarySkillDTO> getSecondarySkills() {
        return secondarySkills;
    }

    public void setSecondarySkills(List<UserSecondarySkillDTO> secondarySkills) {
        this.secondarySkills = secondarySkills;
    }

    public List<UserAncillarySkillDTO> getAncillarySkills() {
        return ancillarySkills;
    }

    public void setAncillarySkills(List<UserAncillarySkillDTO> ancillarySkills) {
        this.ancillarySkills = ancillarySkills;
    }

    public List<UserSkillInfoDTO> getProjectExperiences() {
        return projectExperiences;
    }

    public void setProjectExperiences(List<UserSkillInfoDTO> projectExperiences) {
        this.projectExperiences = projectExperiences;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public List<UserCredentialDTO> getBadges() {
        return badges;
    }
    public void setBadges(List<UserCredentialDTO> badges) {
        this.badges = badges;
    }
    public List<ProfessionalCertificationDTO> getProfessionalCertifications() {
        return professionalCertifications;
    }
    public void setProfessionalCertifications(List<ProfessionalCertificationDTO> professionalCertifications) {
        this.professionalCertifications = professionalCertifications;
    }
    public List<HighImpactAssetDTO> getHighImpactAssets() {
        return highImpactAssets;
    }
    public void setHighImpactAssets(List<HighImpactAssetDTO> highImpactAssets) {
        this.highImpactAssets = highImpactAssets;
    }
} 