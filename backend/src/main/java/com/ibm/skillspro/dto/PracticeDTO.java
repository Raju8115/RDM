package com.ibm.skillspro.dto;

import java.util.List;
import lombok.Data;

@Data
public class PracticeDTO {
    private Long id;
    private String name;
    private String description;
    private List<PracticeAreaDTO> practiceAreas;
} 