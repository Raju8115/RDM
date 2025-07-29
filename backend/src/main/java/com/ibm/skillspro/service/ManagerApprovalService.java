package com.ibm.skillspro.service;

import com.ibm.skillspro.entity.ManagerUserApproval;
import com.ibm.skillspro.repository.ManagerUserApprovalRepository;
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
     * Submit user application to their functional manager
     */
    public ManagerUserApproval submitToManager(String userEmail, String userName, String managerEmail, 
                                             String managerName, String profileData) {
        
        // Check if there's an existing approval for this user
        Optional<ManagerUserApproval> existingApproval = managerUserApprovalRepository.findByUserEmail(userEmail);
        
        if (existingApproval.isPresent()) {
            ManagerUserApproval approval = existingApproval.get();
            
            // If user was previously rejected, mark as updated
            if ("Rejected".equals(approval.getStatus())) {
                approval.setUpdated(true);
                approval.setPreviousProfileData(approval.getProfileData());
            }
            
            // Update the approval
            approval.setProfileData(profileData);
            approval.setUserName(userName);
            approval.setStatus("Pending");
            
            ManagerUserApproval saved = managerUserApprovalRepository.save(approval);
            logger.info("Updated existing approval for user {} to manager {}", userEmail, managerEmail);
            return saved;
        } else {
            // Create new approval
            ManagerUserApproval newApproval = ManagerUserApproval.builder()
                    .userEmail(userEmail)
                    .userName(userName)
                    .managerEmail(managerEmail)
                    .managerName(managerName)
                    .profileData(profileData)
                    .status("Pending")
                    .updated(false)
                    .build();
            
            ManagerUserApproval saved = managerUserApprovalRepository.save(newApproval);
            logger.info("Created new approval for user {} to manager {}", userEmail, managerEmail);
            return saved;
        }
    }

    /**
     * Get all approvals for a specific manager (excluding rejected ones)
     */
    public List<ManagerUserApproval> getApprovalsForManager(String managerEmail) {
        return managerUserApprovalRepository.findByManagerEmailAndStatusNotOrderByUpdatedAtDesc(managerEmail, "Rejected");
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
} 