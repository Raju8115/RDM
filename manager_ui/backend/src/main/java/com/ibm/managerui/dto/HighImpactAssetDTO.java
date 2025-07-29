package com.ibm.managerui.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighImpactAssetDTO {
    private Long id;
    private String title;
    private String businessImpact;
    private String visibilityAdoption;
    private String description;
    private Double impactScore;
} 