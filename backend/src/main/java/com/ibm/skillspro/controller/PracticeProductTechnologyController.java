package com.ibm.skillspro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.ibm.skillspro.service.PracticeProductTechnologyService;
import com.ibm.skillspro.model.PracticeProductTechnology;

@RestController
@RequestMapping("/api/practice-product-technology")
@CrossOrigin(origins = "http://localhost:5173")
public class PracticeProductTechnologyController {

    private final PracticeProductTechnologyService productService;

    public PracticeProductTechnologyController(PracticeProductTechnologyService productService) {
        this.productService = productService;
    }

    @GetMapping("/practice-area/{practiceAreaId}")
    public ResponseEntity<List<PracticeProductTechnology>> getProductsByPracticeArea(@PathVariable Long practiceAreaId) {
        List<PracticeProductTechnology> products = productService.getProductsByPracticeArea(practiceAreaId);
        return ResponseEntity.ok(products);
    }
} 