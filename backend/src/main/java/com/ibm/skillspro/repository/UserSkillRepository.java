package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);
    List<UserSkill> findByPracticeAreaId(Long practiceAreaId);
} 