package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.PendingApproval;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface PendingApprovalRepository extends JpaRepository<PendingApproval, Long> {
    PendingApproval findByEmail(String email);
} 