package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.ProfessionalCertification;
import com.ibm.skillspro.service.ProfessionalCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/professional-certifications")
public class ProfessionalCertificationController {
    @Autowired
    private ProfessionalCertificationService professionalCertificationService;

    @PostMapping
    public ProfessionalCertification addCertification(@RequestBody ProfessionalCertification cert) {
        return professionalCertificationService.addCertification(cert);
    }

    @GetMapping("/user/{userId}")
    public List<ProfessionalCertification> getCertificationsByUserId(@PathVariable Long userId) {
        return professionalCertificationService.getCertificationsByUserId(userId);
    }

    @GetMapping
    public List<ProfessionalCertification> getAllCertifications() {
        return professionalCertificationService.getAllCertifications();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessionalCertification(@PathVariable Long id) {
        professionalCertificationService.deleteCertification(id);
        return ResponseEntity.ok().build();
    }
}