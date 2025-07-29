package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.PracticeProductTechnology;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PracticeProductTechnologyRepository extends JpaRepository<PracticeProductTechnology, Long> {
    List<PracticeProductTechnology> findByPracticeAreaId(Long practiceAreaId);
} 