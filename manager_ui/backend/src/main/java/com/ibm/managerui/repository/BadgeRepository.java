package com.ibm.managerui.repository;

import com.ibm.managerui.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface BadgeRepository extends JpaRepository<Badge, Long> {
} 