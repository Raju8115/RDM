package com.ibm.managerui.controller;

import com.ibm.managerui.entity.PendingApproval;
import com.ibm.managerui.entity.ManagerUserApproval;
import com.ibm.managerui.repository.PendingApprovalRepository;
import com.ibm.managerui.service.ManagerApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PendingApprovalController {
    private final PendingApprovalRepository pendingApprovalRepository;
    private final ManagerApprovalService managerApprovalService;
    private static final Logger logger = LoggerFactory.getLogger(PendingApprovalController.class);

    @GetMapping("/pending-approvals")
    public List<Map<String, Object>> getAllPendingApprovals(
            Authentication authentication,
            @RequestParam(value = "managerEmail", required = false) String queryManagerEmail) {
        
        logger.info("=== DEBUG: Authentication object received ===");
        logger.info("Authentication: {}", authentication);
        if (authentication != null) {
            logger.info("Principal: {}", authentication.getPrincipal());
            logger.info("Principal class: {}", authentication.getPrincipal().getClass().getName());
            logger.info("Authorities: {}", authentication.getAuthorities());
        }
        
        String managerEmail = getManagerEmail(authentication);
        logger.info("Extracted manager email from authentication: {}", managerEmail);
        logger.info("Query parameter manager email: {}", queryManagerEmail);
        
        // Use query parameter if provided, otherwise fall back to authentication or hardcoded value
        if (queryManagerEmail != null && !queryManagerEmail.trim().isEmpty()) {
            managerEmail = queryManagerEmail.trim();
            logger.info("Using manager email from query parameter: {}", managerEmail);
        } else if (managerEmail == null) {
            logger.warn("No manager email found in authentication or query parameter - using Zakir.Hussain1@ibm.com for testing");
            managerEmail = "Zakir.Hussain1@ibm.com";
        }
        
        // Return all approvals for this manager (excluding rejected ones)
        List<ManagerUserApproval> approvals = managerApprovalService.getApprovalsForManager(managerEmail);
        logger.info("Found {} approvals for manager: {}", approvals.size(), managerEmail);
        
        // Transform the data to match frontend expectations
        List<Map<String, Object>> transformedApprovals = new ArrayList<>();
        for (ManagerUserApproval approval : approvals) {
            Map<String, Object> transformed = new HashMap<>();
            transformed.put("id", approval.getId());
            transformed.put("name", approval.getUserName()); // Map userName to name
            transformed.put("email", approval.getUserEmail()); // Map userEmail to email
            transformed.put("status", approval.getStatus());
            transformed.put("updated", approval.isUpdated());
            transformed.put("managerEmail", approval.getManagerEmail());
            transformed.put("managerName", approval.getManagerName());
            transformed.put("profileData", approval.getProfileData());
            transformed.put("previousProfileData", approval.getPreviousProfileData());
            transformed.put("submittedAt", approval.getSubmittedAt());
            transformed.put("updatedAt", approval.getUpdatedAt());
            
            transformedApprovals.add(transformed);
            
            logger.info("Transformed approval - ID: {}, User: {}, Status: {}, Manager: {}", 
                       approval.getId(), approval.getUserEmail(), approval.getStatus(), approval.getManagerEmail());
        }
        
        return transformedApprovals;
    }

    @PostMapping("/pending-approvals")
    public ResponseEntity<?> createPendingApproval(@RequestBody Map<String, Object> request) {
        try {
            logger.info("Received sync request from digital_rdm-demoproject: {}", request);
            
            // Convert Map to ManagerUserApproval entity
            ManagerUserApproval approval = ManagerUserApproval.builder()
                .managerEmail((String) request.get("managerEmail"))
                .managerName((String) request.get("managerName"))
                .userEmail((String) request.get("userEmail"))
                .userName((String) request.get("userName"))
                .status((String) request.get("status"))
                .submittedAt(parseDateTime(request.get("submittedAt")))
                .updatedAt(parseDateTime(request.get("updatedAt")))
                .updated(request.get("updated") != null ? (Boolean) request.get("updated") : false)
                .profileData((String) request.get("profileData"))
                .previousProfileData((String) request.get("previousProfileData"))
                .build();
            
            logger.info("Converted to ManagerUserApproval: user={}, manager={}, status={}", 
                       approval.getUserEmail(), approval.getManagerEmail(), approval.getStatus());
            
            // Sync approval from digital_rdm-demoproject backend
            ManagerUserApproval saved = managerApprovalService.syncApproval(approval);
            logger.info("Successfully synced approval with ID: {} for user {} to manager {}", 
                       saved.getId(), saved.getUserEmail(), saved.getManagerEmail());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error syncing approval: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to sync approval: " + e.getMessage());
        }
    }
    
    // Helper method to parse date/time from various formats
    private java.time.LocalDateTime parseDateTime(Object dateObj) {
        if (dateObj == null) {
            return null;
        }
        
        try {
            if (dateObj instanceof String) {
                String dateStr = (String) dateObj;
                // Try parsing as ISO format first
                return java.time.LocalDateTime.parse(dateStr);
            } else if (dateObj instanceof java.time.LocalDateTime) {
                return (java.time.LocalDateTime) dateObj;
            } else {
                logger.warn("Unknown date format: {}", dateObj.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            logger.warn("Failed to parse date: {} - {}", dateObj, e.getMessage());
            return null;
        }
    }

    @GetMapping("/pending-approvals/{id}")
    public ResponseEntity<?> getPendingApproval(
            @PathVariable Long id, 
            Authentication authentication,
            @RequestParam(value = "managerEmail", required = false) String queryManagerEmail) {
        
        String managerEmail = getManagerEmail(authentication);
        
        // Use query parameter if provided, otherwise fall back to authentication or hardcoded value
        if (queryManagerEmail != null && !queryManagerEmail.trim().isEmpty()) {
            managerEmail = queryManagerEmail.trim();
            logger.info("Using manager email from query parameter for approval {}: {}", id, managerEmail);
        } else if (managerEmail == null) {
            logger.warn("No manager email found in authentication for approval {} - using Zakir.Hussain1@ibm.com for testing", id);
            managerEmail = "Zakir.Hussain1@ibm.com";
        }
        
        Optional<ManagerUserApproval> approvalOpt = managerApprovalService.getApprovalByIdForManager(id, managerEmail);
        if (approvalOpt.isPresent()) {
            ManagerUserApproval approval = approvalOpt.get();
            
            // Fetch highlightFields and removedProductCertifications from digital_rdm-demoproject backend
            String url = "https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/api/manager-approvals/" + id;
            RestTemplate restTemplate = new RestTemplate();
            HashMap<String, Object> remoteApproval = null;
            try {
                remoteApproval = restTemplate.getForObject(url, HashMap.class);
            } catch (Exception e) {
                logger.warn("Could not fetch highlightFields/removedProductCertifications from remote backend: {}", e.getMessage());
            }
            
            HashMap<String, Object> response = new HashMap<>();
            response.put("id", approval.getId());
            response.put("email", approval.getUserEmail());
            response.put("name", approval.getUserName());
            response.put("status", approval.getStatus());
            response.put("updated", approval.isUpdated());
            response.put("profileData", approval.getProfileData());
            response.put("managerEmail", approval.getManagerEmail());
            response.put("managerName", approval.getManagerName());
            
            if (remoteApproval != null && remoteApproval.containsKey("highlightFields")) {
                response.put("highlightFields", remoteApproval.get("highlightFields"));
                logger.info("Forwarding highlightFields for approval {}: {}", id, remoteApproval.get("highlightFields"));
            } else {
                response.put("highlightFields", new String[0]);
                logger.info("No highlightFields found for approval {}", id);
            }
            
            // Forward all removed items if present
            String[] removedItemKeys = {
                "removedProductCertifications", "removedAncillarySkills", "removedProfessionalCertifications",
                "removedBadges", "removedHighImpactAssets", "removedProjectExperiences"
            };
            
            for (String key : removedItemKeys) {
                if (remoteApproval != null && remoteApproval.containsKey(key)) {
                    response.put(key, remoteApproval.get(key));
                    logger.info("Forwarding {} for approval {}: {}", key, id, remoteApproval.get(key));
                } else {
                    response.put(key, new Object[0]);
                    logger.info("No {} found for approval {}", key, id);
                }
            }
            
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/pending-approvals/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> request, 
            Authentication authentication,
            @RequestParam(value = "managerEmail", required = false) String queryManagerEmail) {
        
        String managerEmail = getManagerEmail(authentication);
        
        // Use query parameter if provided, otherwise fall back to authentication or hardcoded value
        if (queryManagerEmail != null && !queryManagerEmail.trim().isEmpty()) {
            managerEmail = queryManagerEmail.trim();
            logger.info("Using manager email from query parameter for status update {}: {}", id, managerEmail);
        } else if (managerEmail == null) {
            logger.warn("No manager email found in authentication for status update {} - using Zakir.Hussain1@ibm.com for testing", id);
            managerEmail = "Zakir.Hussain1@ibm.com";
        }
        
        String status = (String) request.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            if ("Approved".equals(status)) {
                ManagerUserApproval approved = managerApprovalService.approveApplication(id, managerEmail);
                return ResponseEntity.ok(approved);
            } else if ("Rejected".equals(status)) {
                String rejectionReason = (String) request.get("rejectionReason");
                if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Rejection reason is required");
                }
                
                // Get the approval details to get user email
                Optional<ManagerUserApproval> approvalOpt = managerApprovalService.getApprovalByIdForManager(id, managerEmail);
                if (!approvalOpt.isPresent()) {
                    return ResponseEntity.notFound().build();
                }
                
                ManagerUserApproval approval = approvalOpt.get();
                String userEmail = approval.getUserEmail();
                
                // Reject the application
                ManagerUserApproval rejected = managerApprovalService.rejectApplication(id, managerEmail, rejectionReason);
                
                // Send rejection message to digital_rdm-demoproject backend
                sendRejectionMessageToUser(userEmail, rejectionReason, managerEmail);
                
                return ResponseEntity.ok(rejected);
            } else {
                return ResponseEntity.badRequest().body("Invalid status: " + status);
            }
        } catch (SecurityException e) {
            logger.warn("Unauthorized status update attempt: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            logger.error("Error updating status: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    // Helper method to send rejection message to digital_rdm-demoproject backend
    private void sendRejectionMessageToUser(String userEmail, String rejectionReason, String managerEmail) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/api/user-messages";
            
            // Create rejection message
            Map<String, Object> rejectionMessage = new HashMap<>();
            rejectionMessage.put("userEmail", userEmail);
            rejectionMessage.put("reason", rejectionReason);
            rejectionMessage.put("messageType", "REJECTION");
            rejectionMessage.put("read", false);
            rejectionMessage.put("managerEmail", managerEmail);
            
            logger.info("Sending rejection message to user {}: {}", userEmail, rejectionReason);
            
            restTemplate.postForObject(url, rejectionMessage, Object.class);
            logger.info("Successfully sent rejection message to user: {}", userEmail);
        } catch (Exception e) {
            logger.error("Failed to send rejection message to user {}: {}", userEmail, e.getMessage());
        }
    }

    @GetMapping("/pending-approvals/count")
    public ResponseEntity<Map<String, Long>> getPendingCount(
            Authentication authentication,
            @RequestParam(value = "managerEmail", required = false) String queryManagerEmail) {
        
        String managerEmail = getManagerEmail(authentication);
        
        // Use query parameter if provided, otherwise fall back to authentication or hardcoded value
        if (queryManagerEmail != null && !queryManagerEmail.trim().isEmpty()) {
            managerEmail = queryManagerEmail.trim();
            logger.info("Using manager email from query parameter for count: {}", managerEmail);
        } else if (managerEmail == null) {
            logger.warn("No manager email found in authentication for count - using Zakir.Hussain1@ibm.com for testing");
            managerEmail = "Zakir.Hussain1@ibm.com";
        }
        
        long count = managerApprovalService.getPendingApprovalCountForManager(managerEmail);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/pending-approvals/test")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        try {
            // Test if we can access the repository
            long count = managerApprovalService.getPendingApprovalCountForManager("test@ibm.com");
            List<ManagerUserApproval> allApprovals = managerApprovalService.getApprovalsForManager("test@ibm.com");
            
            // Check for specific user data
            List<ManagerUserApproval> zakirApprovals = managerApprovalService.getApprovalsForManager("Zakir.Hussain1@ibm.com");
            Optional<ManagerUserApproval> sandeepApproval = managerApprovalService.getApprovalByIdForManager(1L, "Zakir.Hussain1@ibm.com");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Database connection successful");
            response.put("testCount", count);
            response.put("testApprovals", allApprovals.size());
            response.put("zakirApprovals", zakirApprovals.size());
            response.put("zakirApprovalsList", zakirApprovals);
            response.put("sandeepApproval", sandeepApproval.orElse(null));
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            logger.info("Database test successful - count: {}, approvals: {}, zakirApprovals: {}", 
                       count, allApprovals.size(), zakirApprovals.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Database test failed: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/pending-approvals/test-transformed")
    public ResponseEntity<Map<String, Object>> testTransformedData() {
        try {
            logger.info("=== Testing transformed data for Zakir.Hussain1@ibm.com ===");
            
            // Get approvals for Zakir without authentication
            List<ManagerUserApproval> approvals = managerApprovalService.getApprovalsForManager("Zakir.Hussain1@ibm.com");
            logger.info("Found {} approvals for Zakir", approvals.size());
            
            // Transform the data exactly like the main endpoint
            List<Map<String, Object>> transformedApprovals = new ArrayList<>();
            for (ManagerUserApproval approval : approvals) {
                Map<String, Object> transformed = new HashMap<>();
                transformed.put("id", approval.getId());
                transformed.put("name", approval.getUserName()); // Map userName to name
                transformed.put("email", approval.getUserEmail()); // Map userEmail to email
                transformed.put("status", approval.getStatus());
                transformed.put("updated", approval.isUpdated());
                transformed.put("managerEmail", approval.getManagerEmail());
                transformed.put("managerName", approval.getManagerName());
                transformed.put("profileData", approval.getProfileData());
                transformed.put("previousProfileData", approval.getPreviousProfileData());
                transformed.put("submittedAt", approval.getSubmittedAt());
                transformed.put("updatedAt", approval.getUpdatedAt());
                
                transformedApprovals.add(transformed);
                
                logger.info("Transformed approval - ID: {}, User: {}, Status: {}, Manager: {}", 
                           approval.getId(), approval.getUserEmail(), approval.getStatus(), approval.getManagerEmail());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Transformed data test successful");
            response.put("approvalsCount", transformedApprovals.size());
            response.put("transformedApprovals", transformedApprovals);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Transformed data test failed: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Transformed data test failed: " + e.getMessage());
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Helper method to extract manager email from authentication
     */
    private String getManagerEmail(Authentication authentication) {
        logger.info("=== DEBUG: getManagerEmail called ===");
        
        if (authentication == null) {
            logger.warn("Authentication is null");
            return null;
        }
        
        if (authentication.getPrincipal() == null) {
            logger.warn("Authentication principal is null");
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        logger.info("Principal type: {}", principal.getClass().getName());
        logger.info("Principal: {}", principal);
        
        // Try OidcUser first
        if (principal instanceof OidcUser oidcUser) {
            logger.info("Principal is OidcUser");
            Map<String, Object> claims = oidcUser.getClaims();
            logger.info("OidcUser claims: {}", claims);
            String email = (String) claims.get("email");
            logger.info("Extracted email from OidcUser: {}", email);
            return email;
        }
        
        // Try if principal is a Map (common in some OAuth2 implementations)
        if (principal instanceof Map) {
            logger.info("Principal is Map");
            Map<?, ?> principalMap = (Map<?, ?>) principal;
            logger.info("Principal map: {}", principalMap);
            String email = (String) principalMap.get("email");
            logger.info("Extracted email from Map: {}", email);
            return email;
        }
        
        // Try if principal is a String (email directly)
        if (principal instanceof String) {
            logger.info("Principal is String: {}", principal);
            return (String) principal;
        }
        
        // Try to get email from authentication details
        Object details = authentication.getDetails();
        if (details != null) {
            logger.info("Authentication details: {}", details);
            logger.info("Authentication details class: {}", details.getClass().getName());
        }
        
        logger.warn("Could not extract email from authentication principal");
        return null;
    }
} 
