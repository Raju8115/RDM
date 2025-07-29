package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.Practice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticeRepository extends JpaRepository<Practice, Long> {
} 