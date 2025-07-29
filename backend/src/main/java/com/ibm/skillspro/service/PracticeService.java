package com.ibm.skillspro.service;

import com.ibm.skillspro.entity.Practice;
import com.ibm.skillspro.repository.PracticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.skillspro.dto.PracticeDTO;
import com.ibm.skillspro.dto.PracticeAreaDTO;
import com.ibm.skillspro.dto.PracticeProductTechnologyDTO;
import com.ibm.skillspro.entity.PracticeProductTechnology;

@Service
public class PracticeService {
    private static final Logger logger = LoggerFactory.getLogger(PracticeService.class);
    
    @Autowired
    private PracticeRepository practiceRepository;

    public List<Practice> getAllPractices() {
        logger.info("Fetching all practices from repository");
        List<Practice> practices = practiceRepository.findAll();
        logger.info("Found {} practices", practices.size());
        return practices;
    }

    public Optional<Practice> getPracticeById(Long id) {
        return practiceRepository.findById(id);
    }

    public Practice savePractice(Practice practice) {
        return practiceRepository.save(practice);
    }

    public void deletePractice(Long id) {
        practiceRepository.deleteById(id);
    }

    public List<PracticeDTO> getAllPracticeDTOs() {
        List<Practice> practices = getAllPractices();
        return practices.stream().map(this::toDTO).toList();
    }

    private PracticeDTO toDTO(Practice practice) {
        PracticeDTO dto = new PracticeDTO();
        dto.setId(practice.getId());
        dto.setName(practice.getName());
        dto.setDescription(practice.getDescription());
        
        if (practice.getPracticeAreas() != null) {
            List<PracticeAreaDTO> areaDTOs = practice.getPracticeAreas().stream()
                .map(area -> {
                    PracticeAreaDTO areaDTO = new PracticeAreaDTO();
                    areaDTO.setId(area.getId());
                    areaDTO.setName(area.getName());
                    areaDTO.setDescription(area.getDescription());
                    areaDTO.setPractice_id(practice.getId());
                    
                    if (area.getProducts() != null) {
                        List<PracticeProductTechnologyDTO> productDTOs = area.getProducts().stream()
                            .map(product -> {
                                PracticeProductTechnologyDTO productDTO = new PracticeProductTechnologyDTO();
                                productDTO.setId(product.getId());
                                productDTO.setProduct_name(product.getProduct_name());
                                productDTO.setTechnology_name(product.getTechnology_name());
                                return productDTO;
                            })
                            .toList();
                        areaDTO.setProducts(productDTOs);
                    }
                    return areaDTO;
                })
                .toList();
            dto.setPracticeAreas(areaDTOs);
        }
        return dto;
    }
} 