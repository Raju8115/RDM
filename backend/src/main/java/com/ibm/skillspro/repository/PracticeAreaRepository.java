package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.PracticeArea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PracticeAreaRepository extends JpaRepository<PracticeArea, Long> {
    List<PracticeArea> findByPracticeId(Long practiceId);
} 