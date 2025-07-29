package com.ibm.skillspro.service;

import com.ibm.skillspro.entity.HighImpactAsset;
import com.ibm.skillspro.repository.HighImpactAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighImpactAssetService {
    @Autowired
    private HighImpactAssetRepository highImpactAssetRepository;

    public HighImpactAsset addAsset(HighImpactAsset asset) {
        return highImpactAssetRepository.save(asset);
    }

    public List<HighImpactAsset> getAssetsByUserId(Integer userId) {
        List<HighImpactAsset> assets = highImpactAssetRepository.findByUserId(userId)
                .stream()
                .filter(asset -> !asset.isPendingDelete())
                .collect(java.util.stream.Collectors.toList());
        for (HighImpactAsset asset : assets) {
            asset.setImpactScore(
                    HighImpactAsset.calculateImpactScore(asset.getBusinessImpact(), asset.getVisibilityAdoption()));
        }
        return assets;
    }

    public List<HighImpactAsset> getAllAssets() {
        List<HighImpactAsset> assets = highImpactAssetRepository.findAll();
        for (HighImpactAsset asset : assets) {
            asset.setImpactScore(
                    HighImpactAsset.calculateImpactScore(asset.getBusinessImpact(), asset.getVisibilityAdoption()));
        }
        return assets;
    }

    public void deleteAsset(Integer id) {
        highImpactAssetRepository.deleteById(id);
    }
}