package com.ibm.managerui.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalInfoDTO {
    private Long id;
    private String name;
    private String email;
    private String slackId;
    private String practice;
    private String practiceArea;
    private String practiceProduct;
    private String customerProjects;
    private String selfAssessment;
    private String professionalLevel;
    private List<ProjectDTO> projectExperiences;
    private List<SecondarySkillDTO> secondarySkills;
    private List<AncillarySkillDTO> ancillarySkills;
    private List<ProfessionalCertificationDTO> professionalCertifications;
    private List<BadgeDTO> badges;
    private List<HighImpactAssetDTO> highImpactAssets;
} 