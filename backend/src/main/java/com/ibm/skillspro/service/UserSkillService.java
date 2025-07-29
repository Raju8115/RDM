package com.ibm.skillspro.service;

import com.ibm.skillspro.entity.UserSkill;
import com.ibm.skillspro.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserSkillService {
    @Autowired
    private UserSkillRepository userSkillRepository;

    public List<UserSkill> getAllUserSkills() {
        return userSkillRepository.findAll();
    }

    public List<UserSkill> getUserSkillsByUserId(Long userId) {
        return userSkillRepository.findByUserId(userId);
    }

    public List<UserSkill> getUserSkillsByPracticeAreaId(Long practiceAreaId) {
        return userSkillRepository.findByPracticeAreaId(practiceAreaId);
    }

    public Optional<UserSkill> getUserSkillById(Long id) {
        return userSkillRepository.findById(id);
    }

    public UserSkill saveUserSkill(UserSkill userSkill) {
        return userSkillRepository.save(userSkill);
    }

    public void deleteUserSkill(Long id) {
        userSkillRepository.deleteById(id);
    }
} 