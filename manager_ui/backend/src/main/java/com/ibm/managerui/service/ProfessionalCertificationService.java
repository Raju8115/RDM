package com.ibm.managerui.service;

import com.ibm.managerui.entity.ProfessionalCertification;
import com.ibm.managerui.repository.ProfessionalCertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessionalCertificationService {
    @Autowired
    private ProfessionalCertificationRepository repository;

    public List<ProfessionalCertification> findAll() {
        return repository.findAll();
    }

    public Optional<ProfessionalCertification> findById(Long id) {
        return repository.findById(id);
    }

    public ProfessionalCertification save(ProfessionalCertification cert) {
        return repository.save(cert);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
} 