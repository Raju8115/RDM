package com.ibm.managerui.service;

import com.ibm.managerui.entity.AncillarySkill;
import com.ibm.managerui.repository.AncillarySkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AncillarySkillService {
    @Autowired
    private AncillarySkillRepository repository;

    public List<AncillarySkill> findAll() {
        return repository.findAll();
    }

    public Optional<AncillarySkill> findById(Long id) {
        return repository.findById(id);
    }

    public AncillarySkill save(AncillarySkill skill) {
        return repository.save(skill);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
} 