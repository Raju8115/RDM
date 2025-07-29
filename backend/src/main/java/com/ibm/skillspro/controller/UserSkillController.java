package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.UserSkill;
import com.ibm.skillspro.service.UserSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-skills")
@CrossOrigin(origins = "http://localhost:5173")
public class UserSkillController {
    @Autowired
    private UserSkillService userSkillService;

    @GetMapping
    public List<UserSkill> getAllUserSkills() {
        return userSkillService.getAllUserSkills();
    }

    @GetMapping("/user/{userId}")
    public List<UserSkill> getUserSkillsByUserId(@PathVariable Long userId) {
        return userSkillService.getUserSkillsByUserId(userId);
    }

    @GetMapping("/practice-area/{practiceAreaId}")
    public List<UserSkill> getUserSkillsByPracticeAreaId(@PathVariable Long practiceAreaId) {
        return userSkillService.getUserSkillsByPracticeAreaId(practiceAreaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSkill> getUserSkillById(@PathVariable Long id) {
        return userSkillService.getUserSkillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserSkill createUserSkill(@RequestBody UserSkill userSkill) {
        return userSkillService.saveUserSkill(userSkill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSkill> updateUserSkill(@PathVariable Long id, @RequestBody UserSkill userSkill) {
        return userSkillService.getUserSkillById(id)
                .map(existingUserSkill -> {
                    userSkill.setId(id);
                    return ResponseEntity.ok(userSkillService.saveUserSkill(userSkill));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSkill(@PathVariable Long id) {
        return userSkillService.getUserSkillById(id)
                .map(userSkill -> {
                    userSkillService.deleteUserSkill(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 