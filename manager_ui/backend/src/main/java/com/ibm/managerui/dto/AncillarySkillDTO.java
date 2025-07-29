package com.ibm.managerui.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AncillarySkillDTO {
    private Long id;
    private String technology;
    private String product;
    private Boolean certified;
    private String certificationLink;
    private String certificationLevel;
    private String recencyOfCertification;
    private Double certificationScore;
} 