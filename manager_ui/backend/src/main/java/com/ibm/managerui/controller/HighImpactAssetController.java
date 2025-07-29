package com.ibm.managerui.controller;

import com.ibm.managerui.entity.HighImpactAsset;
import com.ibm.managerui.service.HighImpactAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/high-impact-assets")
public class HighImpactAssetController {
    @Autowired
    private HighImpactAssetService service;

    @GetMapping
    public List<HighImpactAsset> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<HighImpactAsset> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public HighImpactAsset create(@RequestBody HighImpactAsset asset) {
        return service.save(asset);
    }

    @PutMapping("/{id}")
    public HighImpactAsset update(@PathVariable Long id, @RequestBody HighImpactAsset asset) {
        asset.setId(id);
        return service.save(asset);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
} 