package com.ibm.managerui.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professional_info_id")
    private ProfessionalInfo professionalInfo;

    private String projectTitle;
    private String technologiesUsed;
    private String clientTierV2;
    private String projectComplexity;
    private String duration;
    private String responsibilities;
} 