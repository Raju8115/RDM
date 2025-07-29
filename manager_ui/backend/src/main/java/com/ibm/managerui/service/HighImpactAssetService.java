package com.ibm.managerui.service;

import com.ibm.managerui.entity.HighImpactAsset;
import com.ibm.managerui.repository.HighImpactAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HighImpactAssetService {
    @Autowired
    private HighImpactAssetRepository repository;

    public List<HighImpactAsset> findAll() {
        return repository.findAll();
    }

    public Optional<HighImpactAsset> findById(Long id) {
        return repository.findById(id);
    }

    public HighImpactAsset save(HighImpactAsset asset) {
        return repository.save(asset);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
} 