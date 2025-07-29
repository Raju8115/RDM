package com.ibm.managerui.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
    private Long id;
    private String projectTitle;
    private String technologiesUsed;
    private String duration;
    private String responsibilities;
    private String clientTierV2;
    private String projectComplexity;
    private Double projectScore;
} 