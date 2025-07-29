package com.ibm.skillspro.model;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private PracticeArea practiceArea;
} 