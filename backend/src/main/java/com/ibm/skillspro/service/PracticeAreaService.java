package com.ibm.skillspro.service;

import com.ibm.skillspro.entity.PracticeArea;
import com.ibm.skillspro.repository.PracticeAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PracticeAreaService {
    @Autowired
    private PracticeAreaRepository practiceAreaRepository;

    public List<PracticeArea> getAllPracticeAreas() {
        return practiceAreaRepository.findAll();
    }

    public List<PracticeArea> getPracticeAreasByPracticeId(Long practiceId) {
        return practiceAreaRepository.findByPracticeId(practiceId);
    }

    public Optional<PracticeArea> getPracticeAreaById(Long id) {
        return practiceAreaRepository.findById(id);
    }

    public PracticeArea savePracticeArea(PracticeArea practiceArea) {
        return practiceAreaRepository.save(practiceArea);
    }

    public void deletePracticeArea(Long id) {
        practiceAreaRepository.deleteById(id);
    }
} 