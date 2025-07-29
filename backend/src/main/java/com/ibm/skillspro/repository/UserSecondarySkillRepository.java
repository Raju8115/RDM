package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.UserSecondarySkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserSecondarySkillRepository extends JpaRepository<UserSecondarySkill, Long> {
    List<UserSecondarySkill> findByUserId(Long userId);
} 