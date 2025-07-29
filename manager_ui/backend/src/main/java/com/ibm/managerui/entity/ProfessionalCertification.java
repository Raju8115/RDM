package com.ibm.managerui.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professional_info_id")
    private ProfessionalInfo professionalInfo;

    private String title;
    private Boolean certified;
    private String certificationLink;
    private String certificationLevel;
} 