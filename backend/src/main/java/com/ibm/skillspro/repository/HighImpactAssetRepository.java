package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.HighImpactAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HighImpactAssetRepository extends JpaRepository<HighImpactAsset, Integer> {
    List<HighImpactAsset> findByUserId(Integer userId);
}