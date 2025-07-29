package com.ibm.managerui.repository;

import com.ibm.managerui.entity.HighImpactAsset;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface HighImpactAssetRepository extends JpaRepository<HighImpactAsset, Long> {
} 