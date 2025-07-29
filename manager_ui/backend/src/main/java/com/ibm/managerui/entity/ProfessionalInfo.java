package com.ibm.managerui.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "professionalInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projectExperiences = new ArrayList<>();

    @OneToMany(mappedBy = "professionalInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecondarySkill> secondarySkills = new ArrayList<>();

    // Add new relationships for new entities (to be created)
    @OneToMany(mappedBy = "professionalInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AncillarySkill> ancillarySkills = new ArrayList<>();

    @OneToMany(mappedBy = "professionalInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfessionalCertification> professionalCertifications = new ArrayList<>();

    @OneToMany(mappedBy = "professionalInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Badge> badges = new ArrayList<>();

    @OneToMany(mappedBy = "professionalInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HighImpactAsset> highImpactAssets = new ArrayList<>();
} 