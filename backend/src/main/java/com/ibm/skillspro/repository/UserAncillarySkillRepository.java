package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.UserAncillarySkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserAncillarySkillRepository extends JpaRepository<UserAncillarySkill, Long> {
    List<UserAncillarySkill> findByUserId(Long userId);
} 