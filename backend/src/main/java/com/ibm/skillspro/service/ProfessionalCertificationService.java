package com.ibm.skillspro.service;

import com.ibm.skillspro.entity.ProfessionalCertification;
import com.ibm.skillspro.repository.ProfessionalCertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionalCertificationService {
    @Autowired
    private ProfessionalCertificationRepository professionalCertificationRepository;

    public ProfessionalCertification addCertification(ProfessionalCertification cert) {
        return professionalCertificationRepository.save(cert);
    }

    public List<ProfessionalCertification> getCertificationsByUserId(Long userId) {
        List<ProfessionalCertification> certs = professionalCertificationRepository.findByUserId(userId)
                .stream()
                .filter(cert -> !cert.isPendingDelete())
                .collect(java.util.stream.Collectors.toList());
        for (ProfessionalCertification cert : certs) {
            cert.setCertificationScore(
                    ProfessionalCertification.calculateCertificationScore(cert.getCertificationLevel()));
        }
        return certs;
    }

    public List<ProfessionalCertification> getAllCertifications() {
        List<ProfessionalCertification> certs = professionalCertificationRepository.findAll();
        for (ProfessionalCertification cert : certs) {
            cert.setCertificationScore(
                    ProfessionalCertification.calculateCertificationScore(cert.getCertificationLevel()));
        }
        return certs;
    }

    public void deleteCertification(Long id) {
        professionalCertificationRepository.deleteById(id);
    }
}