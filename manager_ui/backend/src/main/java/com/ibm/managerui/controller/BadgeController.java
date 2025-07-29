package com.ibm.managerui.controller;

import com.ibm.managerui.entity.Badge;
import com.ibm.managerui.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {
    @Autowired
    private BadgeService service;

    @GetMapping
    public List<Badge> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Badge> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Badge create(@RequestBody Badge badge) {
        return service.save(badge);
    }

    @PutMapping("/{id}")
    public Badge update(@PathVariable Long id, @RequestBody Badge badge) {
        badge.setId(id);
        return service.save(badge);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
} 