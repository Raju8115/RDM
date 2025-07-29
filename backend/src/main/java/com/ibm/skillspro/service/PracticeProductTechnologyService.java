package com.ibm.skillspro.service;

import com.ibm.skillspro.model.PracticeProductTechnology;
import com.ibm.skillspro.repository.PracticeProductTechnologyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PracticeProductTechnologyService {
    
    private final PracticeProductTechnologyRepository productRepository;

    public PracticeProductTechnologyService(PracticeProductTechnologyRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<PracticeProductTechnology> getProductsByPracticeArea(Long practiceAreaId) {
        return productRepository.findByPracticeAreaId(practiceAreaId).stream()
            .map(entity -> {
                PracticeProductTechnology model = new PracticeProductTechnology();
                model.setId(entity.getId());
                model.setProduct_name(entity.getProduct_name());
                model.setTechnology_name(entity.getTechnology_name());
                model.setPracticeAreaId(entity.getPracticeArea().getId());
                return model;
            })
            .collect(Collectors.toList());
    }
} 