package com.ibm.managerui.service;

import com.ibm.managerui.entity.Badge;
import com.ibm.managerui.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {
    @Autowired
    private BadgeRepository repository;

    public List<Badge> findAll() {
        return repository.findAll();
    }

    public Optional<Badge> findById(Long id) {
        return repository.findById(id);
    }

    public Badge save(Badge badge) {
        return repository.save(badge);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
} 