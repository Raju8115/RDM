package com.ibm.skillspro.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(name = "practice")
public class Practice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "practice", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PracticeArea> practiceAreas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PracticeArea> getPracticeAreas() {
        return practiceAreas;
    }

    public void setPracticeAreas(List<PracticeArea> practiceAreas) {
        this.practiceAreas = practiceAreas;
    }
} 