package com.ibm.managerui.controller;

import com.ibm.managerui.entity.ProfessionalCertification;
import com.ibm.managerui.service.ProfessionalCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/professional-certifications")
public class ProfessionalCertificationController {
    @Autowired
    private ProfessionalCertificationService service;

    @GetMapping
    public List<ProfessionalCertification> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ProfessionalCertification> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ProfessionalCertification create(@RequestBody ProfessionalCertification cert) {
        return service.save(cert);
    }

    @PutMapping("/{id}")
    public ProfessionalCertification update(@PathVariable Long id, @RequestBody ProfessionalCertification cert) {
        cert.setId(id);
        return service.save(cert);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
} 