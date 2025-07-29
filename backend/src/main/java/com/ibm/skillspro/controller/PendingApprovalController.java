package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.PendingApproval;
import com.ibm.skillspro.entity.User;
import com.ibm.skillspro.repository.PendingApprovalRepository;
import com.ibm.skillspro.service.ManagerApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.skillspro.entity.UserMessage;
import com.ibm.skillspro.repository.UserMessageRepository;
import com.ibm.skillspro.service.UserService;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PendingApprovalController {
    private final PendingApprovalRepository pendingApprovalRepository;
    private final UserMessageRepository userMessageRepository;
    private final UserService userService;
    private final ManagerApprovalService managerApprovalService;
    private static final Logger logger = LoggerFactory.getLogger(PendingApprovalController.class);

    @PostMapping("/submit-for-approval")
    public ResponseEntity<?> submitForApproval(@RequestBody PendingApproval request) {
        try {
            // Get functional manager email from session storage (passed from frontend)
            String functionalManagerEmail = request.getFunctionalManagerEmail();
            logger.info("Received approval request for user: {}, functional manager: {}", 
                       request.getEmail(), functionalManagerEmail);
            
            if (functionalManagerEmail == null || functionalManagerEmail.trim().isEmpty()) {
                logger.error("Functional manager email is missing for user: {}", request.getEmail());
                return ResponseEntity.badRequest().body("Functional manager email is required");
            }

            // Submit to the specific functional manager using the new service
            var managerApproval = managerApprovalService.submitToManager(
                request.getEmail(),
                request.getName(),
                functionalManagerEmail,
                "Functional Manager", // We can enhance this later to get actual manager name
                request.getProfileData()
            );

            logger.info("Created manager approval with ID: {} for user: {} to manager: {}", 
                       managerApproval.getId(), request.getEmail(), functionalManagerEmail);

            // Sync to manager_ui backend
            syncToManagerUI(managerApproval);

            logger.info("Successfully submitted application for user {} to manager {}", 
                       request.getEmail(), functionalManagerEmail);

            return ResponseEntity.ok(managerApproval);
        } catch (Exception e) {
            logger.error("Error submitting for approval: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to submit for approval: " + e.getMessage());
        }
    }
    
    // Helper method to sync data to manager_ui backend
    private void syncToManagerUI(com.ibm.skillspro.entity.ManagerUserApproval approval) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8083/api/pending-approvals";
            
            // Create a Map with the approval data to avoid entity type mismatch
            Map<String, Object> approvalData = new HashMap<>();
            approvalData.put("managerEmail", approval.getManagerEmail());
            approvalData.put("managerName", approval.getManagerName());
            approvalData.put("userEmail", approval.getUserEmail());
            approvalData.put("userName", approval.getUserName());
            approvalData.put("status", approval.getStatus());
            
            // Convert LocalDateTime to ISO string format to avoid serialization issues
            if (approval.getSubmittedAt() != null) {
                approvalData.put("submittedAt", approval.getSubmittedAt().toString());
            }
            if (approval.getUpdatedAt() != null) {
                approvalData.put("updatedAt", approval.getUpdatedAt().toString());
            }
            
            approvalData.put("updated", approval.isUpdated());
            approvalData.put("profileData", approval.getProfileData());
            approvalData.put("previousProfileData", approval.getPreviousProfileData());
            
            logger.info("Syncing approval data: {}", approvalData);
            
            restTemplate.postForObject(url, approvalData, Object.class);
            logger.info("Successfully synced manager approval to manager_ui backend for user: {} to manager: {}", 
                       approval.getUserEmail(), approval.getManagerEmail());
        } catch (Exception e) {
            logger.warn("Failed to sync manager approval to manager_ui backend: {}", e.getMessage());
        }
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

    // Helper to compare two secondary skill nodes for equality (by all fields)
    private boolean secondarySkillEquals(JsonNode a, JsonNode b) {
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    // Add comparison methods for all sections
    private boolean ancillarySkillEquals(JsonNode a, JsonNode b) {
        if (a == null || b == null) return false;
        // Compare by ID if available, otherwise by all fields
        if (a.has("id") && b.has("id")) {
            return a.get("id").equals(b.get("id"));
        }
        return a.equals(b);
    }

    private boolean professionalCertificationEquals(JsonNode a, JsonNode b) {
        if (a == null || b == null) return false;
        // Compare by ID if available, otherwise by all fields
        if (a.has("id") && b.has("id")) {
            return a.get("id").equals(b.get("id"));
        }
        return a.equals(b);
    }

    private boolean badgeEquals(JsonNode a, JsonNode b) {
        if (a == null || b == null) return false;
        // Compare by credentialOrderId if available, otherwise by all fields
        if (a.has("credentialOrderId") && b.has("credentialOrderId")) {
            return a.get("credentialOrderId").equals(b.get("credentialOrderId"));
        }
        return a.equals(b);
    }

    private boolean highImpactAssetEquals(JsonNode a, JsonNode b) {
        if (a == null || b == null) return false;
        // Compare by ID if available, otherwise by all fields
        if (a.has("id") && b.has("id")) {
            return a.get("id").equals(b.get("id"));
        }
        return a.equals(b);
    }

    private boolean projectExperienceEquals(JsonNode a, JsonNode b) {
        if (a == null || b == null) return false;
        // Compare by ID if available, otherwise by all fields
        if (a.has("id") && b.has("id")) {
            return a.get("id").equals(b.get("id"));
        }
        return a.equals(b);
    }

    @GetMapping("/pending-approvals")
    public List<Object> getAllPendingApprovals() {
        List<PendingApproval> approvals = pendingApprovalRepository.findAll();
        // Only include approvals with status 'Pending'
        approvals = approvals.stream().filter(a -> "Pending".equalsIgnoreCase(a.getStatus())).toList();
        ObjectMapper mapper = new ObjectMapper();
        return approvals.stream().map(approval -> {
            Set<String> highlightFields = computeJsonDiff(approval.getPreviousProfileData(), approval.getProfileData());
            // Return as a map with highlightFields
            try {
                // Convert approval to map
                java.util.Map<String, Object> map = mapper.convertValue(approval, java.util.Map.class);
                map.put("highlightFields", highlightFields);
                return map;
            } catch (Exception e) {
                return approval;
            }
        }).toList();
    }

    @GetMapping("/pending-approvals/{id}")
    public ResponseEntity<?> getPendingApproval(@PathVariable Long id) {
        Optional<PendingApproval> approvalOpt = pendingApprovalRepository.findById(id);
        if (approvalOpt.isPresent()) {
            PendingApproval approval = approvalOpt.get();
            String oldJson = approval.getPreviousProfileData();
            String newJson = approval.getProfileData();
            Set<String> highlightFields = computeJsonDiff(oldJson, newJson);
            logger.info("[DIFF DEBUG] Approval {}: oldJson={}, newJson={}", id, oldJson, newJson);
            logger.info("[DIFF DEBUG] Approval {}: highlightFields={}", id, highlightFields);
            ObjectMapper mapper = new ObjectMapper();
            java.util.List<JsonNode> removedProductCertifications = new java.util.ArrayList<>();
            java.util.List<JsonNode> removedAncillarySkills = new java.util.ArrayList<>();
            java.util.List<JsonNode> removedProfessionalCertifications = new java.util.ArrayList<>();
            java.util.List<JsonNode> removedBadges = new java.util.ArrayList<>();
            java.util.List<JsonNode> removedHighImpactAssets = new java.util.ArrayList<>();
            java.util.List<JsonNode> removedProjectExperiences = new java.util.ArrayList<>();
            try {
                java.util.Map<String, Object> map = mapper.convertValue(approval, java.util.Map.class);
                map.put("highlightFields", highlightFields);
                // --- Compute removed items for all sections ---
                if (oldJson == null || oldJson.isEmpty()) {
                    logger.warn("[DIFF DEBUG] Approval {}: previousProfileData is missing, cannot compute removed items", id);
                } else {
                    JsonNode oldNode = mapper.readTree(oldJson);
                    JsonNode newNode = newJson != null ? mapper.readTree(newJson) : mapper.createObjectNode();
                    // Product Certifications (secondarySkills)
                    if (oldNode.has("secondarySkills")) {
                        JsonNode oldSkills = oldNode.get("secondarySkills");
                        JsonNode newSkills = newNode.has("secondarySkills") ? newNode.get("secondarySkills") : mapper.createArrayNode();
                        for (JsonNode oldSkill : oldSkills) {
                            boolean found = false;
                            for (JsonNode newSkill : newSkills) {
                                if (secondarySkillEquals(oldSkill, newSkill)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                removedProductCertifications.add(oldSkill);
                            }
                        }
                    }
                    // 3rd Party Certifications (ancillarySkills)
                    if (oldNode.has("ancillarySkills")) {
                        JsonNode oldSkills = oldNode.get("ancillarySkills");
                        JsonNode newSkills = newNode.has("ancillarySkills") ? newNode.get("ancillarySkills") : mapper.createArrayNode();
                        for (JsonNode oldSkill : oldSkills) {
                            boolean found = false;
                            for (JsonNode newSkill : newSkills) {
                                if (ancillarySkillEquals(oldSkill, newSkill)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                removedAncillarySkills.add(oldSkill);
                            }
                        }
                    }
                    // Professional Certifications
                    if (oldNode.has("professionalCertifications")) {
                        JsonNode oldCerts = oldNode.get("professionalCertifications");
                        JsonNode newCerts = newNode.has("professionalCertifications") ? newNode.get("professionalCertifications") : mapper.createArrayNode();
                        for (JsonNode oldCert : oldCerts) {
                            boolean found = false;
                            for (JsonNode newCert : newCerts) {
                                if (professionalCertificationEquals(oldCert, newCert)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                removedProfessionalCertifications.add(oldCert);
                            }
                        }
                    }
                    // Badges
                    if (oldNode.has("badges")) {
                        JsonNode oldBadges = oldNode.get("badges");
                        JsonNode newBadges = newNode.has("badges") ? newNode.get("badges") : mapper.createArrayNode();
                        for (JsonNode oldBadge : oldBadges) {
                            boolean found = false;
                            for (JsonNode newBadge : newBadges) {
                                if (badgeEquals(oldBadge, newBadge)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                removedBadges.add(oldBadge);
                            }
                        }
                    }
                    // High Impact Assets
                    if (oldNode.has("highImpactAssets")) {
                        JsonNode oldAssets = oldNode.get("highImpactAssets");
                        JsonNode newAssets = newNode.has("highImpactAssets") ? newNode.get("highImpactAssets") : mapper.createArrayNode();
                        for (JsonNode oldAsset : oldAssets) {
                            boolean found = false;
                            for (JsonNode newAsset : newAssets) {
                                if (highImpactAssetEquals(oldAsset, newAsset)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                removedHighImpactAssets.add(oldAsset);
                            }
                        }
                    }
                    // Project Experiences
                    if (oldNode.has("projectExperiences")) {
                        JsonNode oldProjects = oldNode.get("projectExperiences");
                        JsonNode newProjects = newNode.has("projectExperiences") ? newNode.get("projectExperiences") : mapper.createArrayNode();
                        for (JsonNode oldProj : oldProjects) {
                            boolean found = false;
                            for (JsonNode newProj : newProjects) {
                                if (projectExperienceEquals(oldProj, newProj)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                removedProjectExperiences.add(oldProj);
                            }
                        }
                    }
                }
                logger.info("[DIFF DEBUG] Approval {}: removedProductCertifications computed: {}", id, removedProductCertifications);
                logger.info("[DIFF DEBUG] Approval {}: removedAncillarySkills computed: {}", id, removedAncillarySkills);
                logger.info("[DIFF DEBUG] Approval {}: removedProfessionalCertifications computed: {}", id, removedProfessionalCertifications);
                logger.info("[DIFF DEBUG] Approval {}: removedBadges computed: {}", id, removedBadges);
                logger.info("[DIFF DEBUG] Approval {}: removedHighImpactAssets computed: {}", id, removedHighImpactAssets);
                logger.info("[DIFF DEBUG] Approval {}: removedProjectExperiences computed: {}", id, removedProjectExperiences);
                map.put("removedProductCertifications", removedProductCertifications);
                map.put("removedAncillarySkills", removedAncillarySkills);
                map.put("removedProfessionalCertifications", removedProfessionalCertifications);
                map.put("removedBadges", removedBadges);
                map.put("removedHighImpactAssets", removedHighImpactAssets);
                map.put("removedProjectExperiences", removedProjectExperiences);
                // --- End removed items logic ---
                return ResponseEntity.ok(map);
            } catch (Exception e) {
                logger.error("[DIFF DEBUG] Exception while computing removed items for approval {}: {}", id, e.getMessage(), e);
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("removedProductCertifications", removedProductCertifications); // always include
                map.put("removedAncillarySkills", removedAncillarySkills);
                map.put("removedProfessionalCertifications", removedProfessionalCertifications);
                map.put("removedBadges", removedBadges);
                map.put("removedHighImpactAssets", removedHighImpactAssets);
                map.put("removedProjectExperiences", removedProjectExperiences);
                return ResponseEntity.ok(map);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/pending-approvals/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody PendingApproval request) {
        Optional<PendingApproval> approvalOpt = pendingApprovalRepository.findById(id);
        if (approvalOpt.isPresent()) {
            PendingApproval approval = approvalOpt.get();
            if (request.getStatus() != null && request.getStatus().equals("Approved")) {
                approval.setStatus("Approved");
                approval.setUpdated(false); // Manager approval resets updated
                // Set previousProfileData to the latest approved profileData
                approval.setPreviousProfileData(approval.getProfileData());
                logger.info("[DIFF DEBUG] Approval {}: previousProfileData set on approval. Value: {}", id, approval.getPreviousProfileData());
                
                // Permanently delete all items marked for deletion after manager approval
                try {
                    // Get user ID from email
                    Optional<User> userOpt = userService.getUserByEmail(approval.getEmail());
                    if (userOpt.isPresent()) {
                        Long userId = userOpt.get().getId();
                        userService.deleteAllPendingItems(userId);
                        logger.info("[DIFF DEBUG] Approval {}: Permanently deleted all pending items for user {} (email: {})", id, userId, approval.getEmail());
                    } else {
                        logger.warn("[DIFF DEBUG] Approval {}: Could not find user for email: {}", id, approval.getEmail());
                    }
                } catch (Exception e) {
                    logger.error("[DIFF DEBUG] Approval {}: Error deleting pending items for user {}: {}", id, approval.getEmail(), e.getMessage(), e);
                }
            } else {
                approval.setStatus("Rejected");
                approval.setUpdated(false);
                approval.setRejectionReason(request.getRejectionReason());
                // Do NOT create UserMessage here. Only manager_ui backend should create the rejection message.
            }
            pendingApprovalRepository.save(approval);
            return ResponseEntity.ok(approval);
        }
        return ResponseEntity.notFound().build();
    }
} 