package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.Practice;
import com.ibm.skillspro.service.PracticeService;
import com.ibm.skillspro.dto.PracticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/practices")
@CrossOrigin(origins = "https://rdm-frontend-2-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com")
public class PracticeController {
    @Autowired
    private PracticeService practiceService;

    @GetMapping
    public List<PracticeDTO> getAllPractices() {
        return practiceService.getAllPracticeDTOs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Practice> getPracticeById(@PathVariable Long id) {
        return practiceService.getPracticeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Practice createPractice(@RequestBody Practice practice) {
        return practiceService.savePractice(practice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Practice> updatePractice(@PathVariable Long id, @RequestBody Practice practice) {
        return practiceService.getPracticeById(id)
                .map(existingPractice -> {
                    practice.setId(id);
                    return ResponseEntity.ok(practiceService.savePractice(practice));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePractice(@PathVariable Long id) {
        return practiceService.getPracticeById(id)
                .map(practice -> {
                    practiceService.deletePractice(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 
