package com.ibm.skillspro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ibm.skillspro.service.ProductService;
import com.ibm.skillspro.model.Product;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/practice-area/{practiceAreaId}")
    public ResponseEntity<List<Product>> getProductsByPracticeArea(@PathVariable Long practiceAreaId) {
        List<Product> products = productService.getProductsByPracticeArea(practiceAreaId);
        return ResponseEntity.ok(products);
    }
} 