package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.UserSkillInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserSkillInfoRepository extends JpaRepository<UserSkillInfo, Long> {
    List<UserSkillInfo> findByUserId(Long userId);
    List<UserSkillInfo> findByUserIdAndUserSkillId(Long userId, Long userSkillId);
} 