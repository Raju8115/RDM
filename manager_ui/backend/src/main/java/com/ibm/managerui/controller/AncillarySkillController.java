package com.ibm.managerui.controller;

import com.ibm.managerui.entity.AncillarySkill;
import com.ibm.managerui.service.AncillarySkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ancillary-skills")
public class AncillarySkillController {
    @Autowired
    private AncillarySkillService service;

    @GetMapping
    public List<AncillarySkill> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<AncillarySkill> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public AncillarySkill create(@RequestBody AncillarySkill skill) {
        return service.save(skill);
    }

    @PutMapping("/{id}")
    public AncillarySkill update(@PathVariable Long id, @RequestBody AncillarySkill skill) {
        skill.setId(id);
        return service.save(skill);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
} 