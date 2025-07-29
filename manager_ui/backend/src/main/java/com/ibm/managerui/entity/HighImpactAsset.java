package com.ibm.managerui.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HighImpactAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professional_info_id")
    private ProfessionalInfo professionalInfo;

    private String title;
    private String businessImpact;
    private String visibilityAdoption;
    private String description;
    private Double impactScore;
} 