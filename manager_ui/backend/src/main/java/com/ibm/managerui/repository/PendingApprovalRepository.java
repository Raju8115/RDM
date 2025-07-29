package com.ibm.managerui.repository;

import com.ibm.managerui.entity.PendingApproval;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface PendingApprovalRepository extends JpaRepository<PendingApproval, Long> {
    PendingApproval findByEmail(String email);
} 