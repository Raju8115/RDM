package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.ManagerUserApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerUserApprovalRepository extends JpaRepository<ManagerUserApproval, Long> {
    
    // Find all approvals for a specific manager
    List<ManagerUserApproval> findByManagerEmailOrderByUpdatedAtDesc(String managerEmail);
    
    // Find all pending approvals for a specific manager
    List<ManagerUserApproval> findByManagerEmailAndStatusOrderByUpdatedAtDesc(String managerEmail, String status);
    
    // Find approval by user email
    Optional<ManagerUserApproval> findByUserEmail(String userEmail);
    
    // Find approval by manager email and user email
    Optional<ManagerUserApproval> findByManagerEmailAndUserEmail(String managerEmail, String userEmail);
    
    // Find all approvals with status not equal to Rejected
    List<ManagerUserApproval> findByManagerEmailAndStatusNotOrderByUpdatedAtDesc(String managerEmail, String status);
} 