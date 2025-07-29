package com.ibm.skillspro.dto;

import java.util.List;
import lombok.Data;

@Data
public class PracticeAreaDTO {
    private Long id;
    private String name;
    private String description;
    private Long practice_id;
    private List<PracticeProductTechnologyDTO> products;
} 