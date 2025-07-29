package com.ibm.managerui.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalCertificationDTO {
    private Long id;
    private String title;
    private Boolean certified;
    private String certificationLink;
    private String certificationLevel;
    private Double certificationScore;
} 