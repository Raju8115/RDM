package com.ibm.skillspro.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "practice_product_technology")
public class PracticeProductTechnology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "practice_area_id", nullable = false)
    @JsonBackReference
    private PracticeArea practiceArea;

    @Column(name = "product_name")
    private String product_name;

    @Column(name = "technology_name")
    private String technology_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PracticeArea getPracticeArea() {
        return practiceArea;
    }

    public void setPracticeArea(PracticeArea practiceArea) {
        this.practiceArea = practiceArea;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getTechnology_name() {
        return technology_name;
    }

    public void setTechnology_name(String technology_name) {
        this.technology_name = technology_name;
    }
} 