package com.ibm.skillspro.model;

public class PracticeProductTechnology {
    private Long id;
    private String product_name;
    private String technology_name;
    private Long practiceAreaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getPracticeAreaId() {
        return practiceAreaId;
    }

    public void setPracticeAreaId(Long practiceAreaId) {
        this.practiceAreaId = practiceAreaId;
    }
} 