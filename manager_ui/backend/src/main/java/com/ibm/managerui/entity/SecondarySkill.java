package com.ibm.managerui.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondarySkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professional_info_id")
    private ProfessionalInfo professionalInfo;

    private String practice;
    private String practiceArea;
    private String products;
    private String duration;
    private String roles;
    private String certificationLevel;
    private String recencyOfCertification;
} 