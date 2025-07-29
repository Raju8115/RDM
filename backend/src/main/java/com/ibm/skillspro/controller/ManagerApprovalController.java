package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.ManagerUserApproval;
import com.ibm.skillspro.service.ManagerApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/api/manager-approvals")
@RequiredArgsConstructor
public class ManagerApprovalController {
    
    private final ManagerApprovalService managerApprovalService;
    private static final Logger logger = LoggerFactory.getLogger(ManagerApprovalController.class);

    /**
     * Get all approvals for the authenticated manager
     */
    @GetMapping
    public ResponseEntity<List<ManagerUserApproval>> getMyApprovals(Authentication authentication) {
        String managerEmail = getManagerEmail(authentication);
        if (managerEmail == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ManagerUserApproval> approvals = managerApprovalService.getApprovalsForManager(managerEmail);
        return ResponseEntity.ok(approvals);
    }

    /**
     * Get pending approvals for the authenticated manager
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ManagerUserApproval>> getMyPendingApprovals(Authentication authentication) {
        String managerEmail = getManagerEmail(authentication);
        if (managerEmail == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ManagerUserApproval> approvals = managerApprovalService.getPendingApprovalsForManager(managerEmail);
        return ResponseEntity.ok(approvals);
    }

    /**
     * Get specific approval by ID (only if manager owns it)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getApprovalById(@PathVariable Long id, Authentication authentication) {
        String managerEmail = getManagerEmail(authentication);
        
        // TEMPORARY: For testing purposes, if no authentication, use Zakir's email
        if (managerEmail == null) {
            logger.warn("No manager email found in authentication for approval {} - using Zakir.Hussain1@ibm.com for testing", id);
            managerEmail = "Zakir.Hussain1@ibm.com";
        }
        
        Optional<ManagerUserApproval> approvalOpt = managerApprovalService.getApprovalByIdForManager(id, managerEmail);
        if (approvalOpt.isPresent()) {
            ManagerUserApproval approval = approvalOpt.get();
            
            // Compute highlight fields if this is an updated submission
            Set<String> highlightFields = new HashSet<>();
            if (approval.isUpdated() && approval.getPreviousProfileData() != null) {
                highlightFields = computeJsonDiff(approval.getPreviousProfileData(), approval.getProfileData());
                logger.info("Computed highlight fields for approval {}: {}", id, highlightFields);
            }
            
            // Create response with highlight data
            Map<String, Object> response = new HashMap<>();
            response.put("id", approval.getId());
            response.put("userEmail", approval.getUserEmail());
            response.put("userName", approval.getUserName());
            response.put("managerEmail", approval.getManagerEmail());
            response.put("managerName", approval.getManagerName());
            response.put("status", approval.getStatus());
            response.put("updated", approval.isUpdated());
            response.put("profileData", approval.getProfileData());
            response.put("previousProfileData", approval.getPreviousProfileData());
            response.put("submittedAt", approval.getSubmittedAt());
            response.put("updatedAt", approval.getUpdatedAt());
            response.put("highlightFields", highlightFields.toArray(new String[0]));
            
            // Add empty arrays for removed items (these are computed in the frontend)
            response.put("removedProductCertifications", new Object[0]);
            response.put("removedAncillarySkills", new Object[0]);
            response.put("removedProfessionalCertifications", new Object[0]);
            response.put("removedBadges", new Object[0]);
            response.put("removedHighImpactAssets", new Object[0]);
            response.put("removedProjectExperiences", new Object[0]);
            
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Approve an application
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ManagerUserApproval> approveApplication(@PathVariable Long id, Authentication authentication) {
        String managerEmail = getManagerEmail(authentication);
        if (managerEmail == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            ManagerUserApproval approved = managerApprovalService.approveApplication(id, managerEmail);
            return ResponseEntity.ok(approved);
        } catch (SecurityException e) {
            logger.warn("Unauthorized approval attempt: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            logger.error("Error approving application: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reject an application
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ManagerUserApproval> rejectApplication(@PathVariable Long id, 
                                                               @RequestBody Map<String, String> request,
                                                               Authentication authentication) {
        String managerEmail = getManagerEmail(authentication);
        if (managerEmail == null) {
            return ResponseEntity.badRequest().build();
        }
        
        String rejectionReason = request.get("rejectionReason");
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            ManagerUserApproval rejected = managerApprovalService.rejectApplication(id, managerEmail, rejectionReason);
            return ResponseEntity.ok(rejected);
        } catch (SecurityException e) {
            logger.warn("Unauthorized rejection attempt: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            logger.error("Error rejecting application: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get pending approval count for the authenticated manager
     */
    @GetMapping("/count/pending")
    public ResponseEntity<Map<String, Long>> getPendingCount(Authentication authentication) {
        String managerEmail = getManagerEmail(authentication);
        if (managerEmail == null) {
            return ResponseEntity.badRequest().build();
        }
        
        long count = managerApprovalService.getPendingApprovalCountForManager(managerEmail);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Test endpoint to check manager approval data
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testManagerApprovals() {
        try {
            // Get all approvals for testing
            List<ManagerUserApproval> allApprovals = managerApprovalService.getApprovalsForManager("test@ibm.com");
            
            // Also check for specific user
            Optional<ManagerUserApproval> sandeepApproval = managerApprovalService.getApprovalByIdForManager(1L, "Zakir.Hussain1@ibm.com");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Manager approval service is working");
            response.put("totalApprovals", allApprovals.size());
            response.put("approvals", allApprovals);
            response.put("sandeepApproval", sandeepApproval.orElse(null));
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            logger.info("Manager approval test successful - total approvals: {}", allApprovals.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Manager approval test failed: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Manager approval test failed: " + e.getMessage());
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Helper method to extract manager email from authentication
     */
    private String getManagerEmail(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            return oidcUser.getEmail();
        }
        return null;
    }

    // Helper to compute diff between two JSON objects (returns set of changed/added field paths)
    private Set<String> computeJsonDiff(String oldJson, String newJson) {
        Set<String> diffs = new HashSet<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode oldNode = oldJson != null ? mapper.readTree(oldJson) : mapper.createObjectNode();
            JsonNode newNode = newJson != null ? mapper.readTree(newJson) : mapper.createObjectNode();
            computeJsonDiffRecursive("", oldNode, newNode, diffs);
        } catch (Exception e) {
            // Ignore diff errors
        }
        return diffs;
    }
    
    private void computeJsonDiffRecursive(String path, JsonNode oldNode, JsonNode newNode, Set<String> diffs) {
        if (newNode.isObject()) {
            newNode.fieldNames().forEachRemaining(field -> {
                String childPath = path.isEmpty() ? field : path + "." + field;
                JsonNode oldChild = oldNode.has(field) ? oldNode.get(field) : null;
                JsonNode newChild = newNode.get(field);
                if (oldChild == null) {
                    // New field: add all subfields recursively
                    addAllPathsRecursive(childPath, newChild, diffs);
                } else {
                    computeJsonDiffRecursive(childPath, oldChild, newChild, diffs);
                }
            });
        } else if (newNode.isArray()) {
            for (int i = 0; i < newNode.size(); i++) {
                String childPath = path + "[" + i + "]";
                JsonNode oldChild = (oldNode.isArray() && oldNode.size() > i) ? oldNode.get(i) : null;
                JsonNode newChild = newNode.get(i);
                if (oldChild == null) {
                    // New array element: add all subfields recursively
                    addAllPathsRecursive(childPath, newChild, diffs);
                } else {
                    computeJsonDiffRecursive(childPath, oldChild, newChild, diffs);
                }
            }
        } else {
            if (!oldNode.equals(newNode)) {
                diffs.add(path); // Changed value
            }
        }
    }
    
    // Helper to add all subfield paths for a new object/array
    private void addAllPathsRecursive(String path, JsonNode node, Set<String> diffs) {
        if (node.isObject()) {
            node.fieldNames().forEachRemaining(field -> {
                String childPath = path + "." + field;
                addAllPathsRecursive(childPath, node.get(field), diffs);
            });
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                String childPath = path + "[" + i + "]";
                addAllPathsRecursive(childPath, node.get(i), diffs);
            }
        } else {
            diffs.add(path);
        }
    }
} 