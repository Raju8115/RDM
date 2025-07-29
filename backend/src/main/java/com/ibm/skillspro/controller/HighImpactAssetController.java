package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.HighImpactAsset;
import com.ibm.skillspro.service.HighImpactAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/high-impact-assets")
public class HighImpactAssetController {
    @Autowired
    private HighImpactAssetService highImpactAssetService;

    @PostMapping
    public HighImpactAsset addAsset(@RequestBody HighImpactAsset asset) {
        return highImpactAssetService.addAsset(asset);
    }

    @GetMapping("/user/{userId}")
    public List<HighImpactAsset> getAssetsByUserId(@PathVariable Integer userId) {
        return highImpactAssetService.getAssetsByUserId(userId);
    }

    @GetMapping
    public List<HighImpactAsset> getAllAssets() {
        return highImpactAssetService.getAllAssets();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHighImpactAsset(@PathVariable Integer id) {
        highImpactAssetService.deleteAsset(id);
        return ResponseEntity.ok().build();
    }
}