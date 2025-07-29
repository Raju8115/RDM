package com.ibm.managerui.service;

import com.ibm.managerui.entity.ManagerUserApproval;
import com.ibm.managerui.repository.ManagerUserApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerApprovalService {
    
    private final ManagerUserApprovalRepository managerUserApprovalRepository;
    private static final Logger logger = LoggerFactory.getLogger(ManagerApprovalService.class);

    /**
     * Get all approvals for a specific manager (excluding rejected ones)
     */
    public List<ManagerUserApproval> getApprovalsForManager(String managerEmail) {
        logger.info("Getting approvals for manager: {}", managerEmail);
        List<ManagerUserApproval> approvals = managerUserApprovalRepository.findByManagerEmailAndStatusNotOrderByUpdatedAtDesc(managerEmail, "Rejected");
        logger.info("Found {} approvals for manager {} (excluding rejected)", approvals.size(), managerEmail);
        return approvals;
    }

    /**
     * Get pending approvals for a specific manager
     */
    public List<ManagerUserApproval> getPendingApprovalsForManager(String managerEmail) {
        return managerUserApprovalRepository.findByManagerEmailAndStatusOrderByUpdatedAtDesc(managerEmail, "Pending");
    }

    /**
     * Approve a user application
     */
    public ManagerUserApproval approveApplication(Long approvalId, String managerEmail) {
        Optional<ManagerUserApproval> approvalOpt = managerUserApprovalRepository.findById(approvalId);
        
        if (approvalOpt.isPresent()) {
            ManagerUserApproval approval = approvalOpt.get();
            
            // Verify this manager owns this approval
            if (!managerEmail.equalsIgnoreCase(approval.getManagerEmail())) {
                throw new SecurityException("Manager not authorized to approve this application");
            }
            
            approval.setStatus("Approved");
            approval.setUpdated(false);
            
            ManagerUserApproval saved = managerUserApprovalRepository.save(approval);
            logger.info("Manager {} approved application for user {}", managerEmail, approval.getUserEmail());
            return saved;
        }
        
        throw new RuntimeException("Approval not found with ID: " + approvalId);
    }

    /**
     * Reject a user application (this will remove it from manager's view)
     */
    public ManagerUserApproval rejectApplication(Long approvalId, String managerEmail, String rejectionReason) {
        Optional<ManagerUserApproval> approvalOpt = managerUserApprovalRepository.findById(approvalId);
        
        if (approvalOpt.isPresent()) {
            ManagerUserApproval approval = approvalOpt.get();
            
            // Verify this manager owns this approval
            if (!managerEmail.equalsIgnoreCase(approval.getManagerEmail())) {
                throw new SecurityException("Manager not authorized to reject this application");
            }
            
            approval.setStatus("Rejected");
            approval.setUpdated(false);
            
            ManagerUserApproval saved = managerUserApprovalRepository.save(approval);
            logger.info("Manager {} rejected application for user {} with reason: {}", 
                       managerEmail, approval.getUserEmail(), rejectionReason);
            return saved;
        }
        
        throw new RuntimeException("Approval not found with ID: " + approvalId);
    }

    /**
     * Get approval by ID for a specific manager
     */
    public Optional<ManagerUserApproval> getApprovalByIdForManager(Long approvalId, String managerEmail) {
        Optional<ManagerUserApproval> approvalOpt = managerUserApprovalRepository.findById(approvalId);
        
        if (approvalOpt.isPresent()) {
            ManagerUserApproval approval = approvalOpt.get();
            
            // Verify this manager owns this approval
            if (managerEmail.equalsIgnoreCase(approval.getManagerEmail())) {
                return approvalOpt;
            }
        }
        
        return Optional.empty();
    }

    /**
     * Get approval count for a manager
     */
    public long getPendingApprovalCountForManager(String managerEmail) {
        return managerUserApprovalRepository.findByManagerEmailAndStatusOrderByUpdatedAtDesc(managerEmail, "Pending").size();
    }

    /**
     * Sync approval from digital_rdm-demoproject backend
     */
    public ManagerUserApproval syncApproval(ManagerUserApproval approval) {
        logger.info("Syncing approval for user: {} to manager: {}", approval.getUserEmail(), approval.getManagerEmail());
        
        // Check if approval already exists
        Optional<ManagerUserApproval> existingApproval = managerUserApprovalRepository.findByUserEmail(approval.getUserEmail());
        
        if (existingApproval.isPresent()) {
            ManagerUserApproval existing = existingApproval.get();
            logger.info("Updating existing approval with ID: {}", existing.getId());
            existing.setProfileData(approval.getProfileData());
            existing.setUserName(approval.getUserName());
            existing.setStatus(approval.getStatus());
            existing.setUpdated(approval.isUpdated());
            if (approval.getPreviousProfileData() != null) {
                existing.setPreviousProfileData(approval.getPreviousProfileData());
            }
            ManagerUserApproval saved = managerUserApprovalRepository.save(existing);
            logger.info("Updated approval with ID: {}", saved.getId());
            return saved;
        } else {
            logger.info("Creating new approval for user: {}", approval.getUserEmail());
            ManagerUserApproval saved = managerUserApprovalRepository.save(approval);
            logger.info("Created new approval with ID: {}", saved.getId());
            return saved;
        }
    }
} 