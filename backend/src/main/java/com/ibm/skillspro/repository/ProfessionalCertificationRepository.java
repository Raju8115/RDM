package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.ProfessionalCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfessionalCertificationRepository extends JpaRepository<ProfessionalCertification, Long> {
    List<ProfessionalCertification> findByUserId(Long userId);
}