package com.ibm.managerui.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondarySkillDTO {
    private Long id;
    private String practice;
    private String practiceArea;
    private String productsTechnologies;
    private String duration;
    private String roles;
    private String certificationLevel;
    private String recencyOfCertification;
    private Double certificationScore;
} 