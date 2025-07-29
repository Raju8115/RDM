package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.PracticeArea;
import com.ibm.skillspro.service.PracticeAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/practice-areas")
@CrossOrigin(origins = "http://localhost:5173")
public class PracticeAreaController {
    @Autowired
    private PracticeAreaService practiceAreaService;

    @GetMapping
    public List<PracticeArea> getAllPracticeAreas() {
        return practiceAreaService.getAllPracticeAreas();
    }

    @GetMapping("/practice/{practiceId}")
    public List<PracticeArea> getPracticeAreasByPracticeId(@PathVariable Long practiceId) {
        return practiceAreaService.getPracticeAreasByPracticeId(practiceId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PracticeArea> getPracticeAreaById(@PathVariable Long id) {
        return practiceAreaService.getPracticeAreaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PracticeArea createPracticeArea(@RequestBody PracticeArea practiceArea) {
        return practiceAreaService.savePracticeArea(practiceArea);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PracticeArea> updatePracticeArea(@PathVariable Long id, @RequestBody PracticeArea practiceArea) {
        return practiceAreaService.getPracticeAreaById(id)
                .map(existingPracticeArea -> {
                    practiceArea.setId(id);
                    return ResponseEntity.ok(practiceAreaService.savePracticeArea(practiceArea));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePracticeArea(@PathVariable Long id) {
        return practiceAreaService.getPracticeAreaById(id)
                .map(practiceArea -> {
                    practiceAreaService.deletePracticeArea(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 