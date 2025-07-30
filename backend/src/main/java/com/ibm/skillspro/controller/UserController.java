package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.User;
import com.ibm.skillspro.entity.UserSkill;
import com.ibm.skillspro.entity.UserSkillInfo;
import com.ibm.skillspro.service.UserService;
import com.ibm.skillspro.dto.UserProfileDTO;
import com.ibm.skillspro.dto.UserSkillInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.security.core.Authentication;
import com.ibm.skillspro.dto.UserAncillarySkillDTO;
import com.ibm.skillspro.entity.UserAncillarySkill;
import com.ibm.skillspro.repository.UserAncillarySkillRepository;
import com.ibm.skillspro.repository.UserRepository;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import com.ibm.skillspro.dto.UserCredentialDTO;
import com.ibm.skillspro.service.UserCredentialService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserAncillarySkillRepository userAncillarySkillRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCredentialService userCredentialService;
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // @GetMapping("/user")
    // public Object getUser(Authentication authentication) {
    //     if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
    //         // If not authenticated, Spring Security will handle redirect in browser
    //         return Map.of("error", "Not authenticated");
    //     }

    //     Object principal = authentication.getPrincipal();
    //     System.out.println("AUTH >>> " + principal);

    //     String email = null;
    //     String name = null;
    //     String slackId = null;
    //     if (principal instanceof OidcUser oidcUser) {
    //         email = (String) oidcUser.getClaims().get("email");
    //     }
    //     if (email == null)
    //         return "No email found";

    //     // Check if user exists in DB
    //     Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
    //     User user;
    //     if (userOpt.isPresent()) {
    //         user = userOpt.get();
    //         logger.info("User found in DB: {}", user);
    //     } else {
    //         // Fetch from IBM profile API
    //         RestTemplate restTemplate = new RestTemplate();
    //         String url = "https://w3-unified-profile-api.ibm.com/v3/profiles/" + email + "/profile";
    //         try {
    //             Map<?, ?> ibmProfile = restTemplate.getForObject(url, Map.class);
    //             Map<?, ?> content = (Map<?, ?>) ibmProfile.get("content");
    //             if (content != null) {
    //                 Object nameObj = content.get("nameDisplay");
    //                 Object slackObj = content.get("preferredSlackUsername");
    //                 name = nameObj != null ? (String) nameObj : "";
    //                 slackId = slackObj != null ? (String) slackObj : "";
    //             } else {
    //                 name = "";
    //                 slackId = "";
    //             }
    //         } catch (Exception e) {
    //             System.err.println("Failed to fetch IBM profile: " + e.getMessage());
    //             name = "";
    //             slackId = "";
    //         }
    //         user = new User();
    //         user.setEmail(email);
    //         user.setName(name);
    //         user.setSlackId(slackId);
    //         logger.info("Creating new user: {}", user);
    //         userRepository.save(user);
    //         logger.info("User saved to DB: {}", user);
    //         // Fetch a default practice_area and product_technology for valid foreign keys
    //         UserSkill defaultSkill = new UserSkill();
    //         defaultSkill.setUserId(user.getId());
    //         defaultSkill.setPracticeId(null);
    //         defaultSkill.setPracticeAreaId(null);
    //         defaultSkill.setPracticeProductTechnologyId(null);
    //         defaultSkill.setProjectsDone("");
    //         defaultSkill.setSelfAssessmentLevel("");
    //         defaultSkill.setProfessionalLevel("");
    //         userService.saveUserSkill(defaultSkill);
    //         logger.info("Default primary skill created for new user: {}", defaultSkill);
    //         // Create a default user_skill_info row
    //         UserSkillInfo defaultSkillInfo = new UserSkillInfo();
    //         defaultSkillInfo.setUserId(user.getId());
    //         defaultSkillInfo.setUserSkillId(defaultSkill.getId());
    //         defaultSkillInfo.setProjectTitle("");
    //         defaultSkillInfo.setTechnologiesUsed("");
    //         defaultSkillInfo.setDuration("");
    //         defaultSkillInfo.setResponsibilities("");
    //         userService.saveUserSkillInfo(defaultSkillInfo);
    //         logger.info("Default user_skill_info created for new user: {}", defaultSkillInfo);
    //     }
    //     return Map.of(
    //             "id", user.getId(),
    //             "email", user.getEmail(),
    //             "name", user.getName(),
    //             "slackId", user.getSlackId());
    // }

@GetMapping("/user")
public Object getUser(Authentication authentication, HttpServletResponse response) throws IOException {
    // If not logged in → redirect to OAuth2 login
    if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
        response.sendRedirect("/oauth2/authorization/appid");
        return null; // Browser will redirect
    }

    String email = oidcUser.getEmail();
    String name = oidcUser.getFullName();
    String slackId = "";

    if (email == null) {
        return Map.of("error", "No email found");
    }

    // If name not present in token → try IBM Unified Profile API
    if (name == null || name.isBlank()) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://w3-unified-profile-api.ibm.com/v3/profiles/" + email + "/profile";
            Map<?, ?> ibmProfile = restTemplate.getForObject(url, Map.class);

            if (ibmProfile != null && ibmProfile.containsKey("content")) {
                Map<?, ?> content = (Map<?, ?>) ibmProfile.get("content");
                if (content != null) {
                    Object nameObj = content.get("nameDisplay");
                    Object slackObj = content.get("preferredSlackUsername");
                    if (nameObj != null) name = nameObj.toString();
                    if (slackObj != null) slackId = slackObj.toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch IBM profile: " + e.getMessage());
        }
    }

    // Lookup in DB
    Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
    User user;
    if (userOpt.isPresent()) {
        user = userOpt.get();
        logger.info("User found in DB: {}", user);
    } else {
        // Create new user
        user = new User();
        user.setEmail(email);
        user.setName(name != null ? name : "");
        user.setSlackId(slackId);
        userRepository.save(user);
        logger.info("Created new user: {}", user);

        // Create default UserSkill
        UserSkill defaultSkill = new UserSkill();
        defaultSkill.setUserId(user.getId());
        defaultSkill.setPracticeId(null);
        defaultSkill.setPracticeAreaId(null);
        defaultSkill.setPracticeProductTechnologyId(null);
        defaultSkill.setProjectsDone("");
        defaultSkill.setSelfAssessmentLevel("");
        defaultSkill.setProfessionalLevel("");
        userService.saveUserSkill(defaultSkill);

        // Create default UserSkillInfo
        UserSkillInfo defaultSkillInfo = new UserSkillInfo();
        defaultSkillInfo.setUserId(user.getId());
        defaultSkillInfo.setUserSkillId(defaultSkill.getId());
        defaultSkillInfo.setProjectTitle("");
        defaultSkillInfo.setTechnologiesUsed("");
        defaultSkillInfo.setDuration("");
        defaultSkillInfo.setResponsibilities("");
        userService.saveUserSkillInfo(defaultSkillInfo);

        logger.info("Default skills created for new user: {}", defaultSkill);
    }

    // Return user info
    return Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "name", user.getName(),
            "slackId", user.getSlackId()
    );
}



    @GetMapping("/userinfo")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        if (authentication == null)
            return Map.of("error", "Not authenticated");

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getClaims(); // ✅ safe, well-typed
        }

        return Map.of("error", "User is not an OIDC user");
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<UserProfileDTO> getProfileByEmail(@PathVariable String email) {
        System.out.println("Received request for email: " + email);
        // Determine if user is new (missing required professional info)
        return userService.getUserByEmail(email)
                .map(user -> {
                    boolean isNewUser = false;
                    // Check if user is missing required professional info
                    List<UserSkill> primarySkills = userService.getUserSkillsByUserId(user.getId());
                    if (primarySkills.isEmpty()) {
                        isNewUser = true;
                    } else {
                        UserSkill mainSkill = primarySkills.get(0);
                        if (mainSkill.getPracticeAreaId() == null
                                || mainSkill.getPracticeProductTechnologyId() == null) {
                            isNewUser = true;
                        }
                    }
                    return userService.getUserProfileByEmail(email, isNewUser)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{email}")
    public ResponseEntity<UserProfileDTO> updateProfileByEmail(@PathVariable String email,
            @RequestBody UserProfileDTO updatedProfileDTO) {
        return userService.getUserByEmail(email)
                .map(existingUser -> {
                    // Update basic user information
                    if (updatedProfileDTO.getName() != null) {
                        existingUser.setName(updatedProfileDTO.getName());
                    }
                    if (updatedProfileDTO.getSlackId() != null) {
                        existingUser.setSlackId(updatedProfileDTO.getSlackId());
                    }
                    userService.saveUser(existingUser);

                    // Update the user's primary skill information
                    List<UserSkill> primarySkills = userService.getUserSkillsByUserId(existingUser.getId());
                    UserSkill mainSkill = null;
                    if (!primarySkills.isEmpty()) {
                        mainSkill = primarySkills.get(0);
                        if (updatedProfileDTO.getPracticeId() != null) {
                            mainSkill.setPracticeId(updatedProfileDTO.getPracticeId());
                        }
                        if (updatedProfileDTO.getPracticeAreaId() != null) {
                            mainSkill.setPracticeAreaId(updatedProfileDTO.getPracticeAreaId());
                        }
                        if (updatedProfileDTO.getPracticeProductTechnologyId() != null) {
                            mainSkill
                                    .setPracticeProductTechnologyId(updatedProfileDTO.getPracticeProductTechnologyId());
                        }
                        if (updatedProfileDTO.getProjectsDone() != null) {
                            mainSkill.setProjectsDone(updatedProfileDTO.getProjectsDone());
                        }
                        if (updatedProfileDTO.getSelfAssessmentLevel() != null) {
                            mainSkill.setSelfAssessmentLevel(updatedProfileDTO.getSelfAssessmentLevel());
                        }
                        if (updatedProfileDTO.getProfessionalLevel() != null) {
                            mainSkill.setProfessionalLevel(updatedProfileDTO.getProfessionalLevel());
                        }
                        userService.saveUserSkill(mainSkill);
                    } else {
                        // No primary skill exists for this user, create one
                        if (updatedProfileDTO.getPracticeAreaId() == null
                                || updatedProfileDTO.getPracticeProductTechnologyId() == null) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "practiceAreaId and practiceProductTechnologyId are required for new user skill");
                        }
                        mainSkill = new UserSkill();
                        mainSkill.setUserId(existingUser.getId());
                        mainSkill.setPracticeId(updatedProfileDTO.getPracticeId());
                        mainSkill.setPracticeAreaId(updatedProfileDTO.getPracticeAreaId());
                        mainSkill.setPracticeProductTechnologyId(updatedProfileDTO.getPracticeProductTechnologyId());
                        mainSkill.setProjectsDone(updatedProfileDTO.getProjectsDone());
                        mainSkill.setSelfAssessmentLevel(updatedProfileDTO.getSelfAssessmentLevel());
                        mainSkill.setProfessionalLevel(updatedProfileDTO.getProfessionalLevel());
                        userService.saveUserSkill(mainSkill);
                    }

                    // Update project experiences
                    List<UserSkillInfo> existingProjectExperiences = userService
                            .getUserSkillInfoByUserId(existingUser.getId());
                    List<UserSkillInfoDTO> updatedProjectExperiencesDTO = updatedProfileDTO.getProjectExperiences();

                    // Identify projects to delete (present in DB but not in DTO) - SOFT DELETE
                    for (UserSkillInfo existingProject : existingProjectExperiences) {
                        boolean found = false;
                        for (UserSkillInfoDTO updatedProjectDTO : updatedProjectExperiencesDTO) {
                            if (updatedProjectDTO.getId() != null && !updatedProjectDTO.getId().startsWith("temp-")) {
                                try {
                                    Long updatedProjectId = Long.parseLong(updatedProjectDTO.getId());
                                    if (existingProject.getId() != null
                                            && existingProject.getId().equals(updatedProjectId)) {
                                        found = true;
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println(
                                            "Warning: Non-numeric ID in DTO for existing project during deletion check: "
                                                    + updatedProjectDTO.getId());
                                }
                            }
                        }
                        if (!found) {
                            // Soft delete: mark as pendingDelete = true
                            existingProject.setPendingDelete(true);
                            userService.saveUserSkillInfo(existingProject);
                        }
                    }

                    // Add or update projects
                    for (UserSkillInfoDTO updatedProjectDTO : updatedProjectExperiencesDTO) {
                        UserSkillInfo projectToSave;
                        Long projectIdToUse = null; // Initialize to null

                        if (updatedProjectDTO.getId() != null && !updatedProjectDTO.getId().startsWith("temp-")) {
                            try {
                                projectIdToUse = Long.parseLong(updatedProjectDTO.getId());
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Error: Invalid project ID format for update: " + updatedProjectDTO.getId());
                                // projectIdToUse remains null if parsing fails
                            }
                        }

                        if (projectIdToUse != null) {
                            // Update existing project: find by parsed ID
                            final Long finalProjectIdToUse = projectIdToUse; // Create a final copy for stream
                            projectToSave = existingProjectExperiences.stream()
                                    .filter(p -> p.getId() != null && p.getId().equals(finalProjectIdToUse))
                                    .findFirst()
                                    .orElse(new UserSkillInfo()); // Should ideally find an existing one
                            projectToSave.setId(projectIdToUse); // Set the parsed Long ID
                            // Reset pendingDelete to false when updating
                            projectToSave.setPendingDelete(false);
                        } else {
                            // New project: either ID is null or a temp- string
                            projectToSave = new UserSkillInfo();
                            projectToSave.setId(null); // Ensure ID is null for new projects so JPA generates it
                            projectToSave.setPendingDelete(false);
                        }
                        projectToSave.setUserId(existingUser.getId());
                        if (mainSkill != null) {
                            projectToSave.setUserSkillId(mainSkill.getId());
                        } else if (!primarySkills.isEmpty()) {
                            projectToSave.setUserSkillId(primarySkills.get(0).getId());
                        } else {
                            logger.error(
                                    "No primary skill found for user {} when saving project experience. Skipping save.",
                                    email);
                            continue; // Skip saving this project experience
                        }
                        projectToSave.setProjectTitle(updatedProjectDTO.getProjectTitle());
                        projectToSave.setTechnologiesUsed(updatedProjectDTO.getTechnologiesUsed());
                        projectToSave.setDuration(updatedProjectDTO.getDuration());
                        projectToSave.setResponsibilities(updatedProjectDTO.getResponsibilities());
                        projectToSave.setClientTierV2(updatedProjectDTO.getClientTierV2());
                        projectToSave.setProjectComplexity(updatedProjectDTO.getProjectComplexity());
                        userService.saveUserSkillInfo(projectToSave);
                    }

                    // Update secondary skills
                    List<com.ibm.skillspro.entity.UserSecondarySkill> existingSecondarySkills = userService
                            .getUserSecondarySkillsByUserId(existingUser.getId());
                    List<com.ibm.skillspro.dto.UserSecondarySkillDTO> updatedSecondarySkillsDTO = updatedProfileDTO
                            .getSecondarySkills();

                    // Ensure updatedSecondarySkillsDTO is not null
                    if (updatedSecondarySkillsDTO == null) {
                        updatedSecondarySkillsDTO = java.util.Collections.emptyList();
                    }

                    // Identify secondary skills to delete (present in DB but not in DTO) - SOFT DELETE
                    for (com.ibm.skillspro.entity.UserSecondarySkill existingSkill : existingSecondarySkills) {
                        boolean found = false;
                        for (com.ibm.skillspro.dto.UserSecondarySkillDTO updatedSkillDTO : updatedSecondarySkillsDTO) {
                            if (updatedSkillDTO.getId() != null && !updatedSkillDTO.getId().startsWith("temp-")) {
                                try {
                                    Long updatedSkillId = Long.parseLong(updatedSkillDTO.getId());
                                    if (existingSkill.getId() != null && existingSkill.getId().equals(updatedSkillId)) {
                                        found = true;
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println(
                                            "Warning: Non-numeric ID in DTO for existing secondary skill during deletion check: "
                                                    + updatedSkillDTO.getId());
                                }
                            }
                        }
                        if (!found) {
                            // Soft delete: mark as pendingDelete = true
                            existingSkill.setPendingDelete(true);
                            userService.saveUserSecondarySkill(existingSkill);
                        }
                    }

                    // Add or update secondary skills
                    for (com.ibm.skillspro.dto.UserSecondarySkillDTO updatedSkillDTO : updatedSecondarySkillsDTO) {
                        com.ibm.skillspro.entity.UserSecondarySkill skillToSave;
                        Long skillIdToUse = null; // Initialize to null

                        if (updatedSkillDTO.getId() != null && !updatedSkillDTO.getId().startsWith("temp-")) {
                            try {
                                skillIdToUse = Long.parseLong(updatedSkillDTO.getId());
                            } catch (NumberFormatException e) {
                                System.err.println("Error: Invalid secondary skill ID format for update: "
                                        + updatedSkillDTO.getId());
                            }
                        }

                        if (skillIdToUse != null) {
                            // Update existing secondary skill: find by parsed ID
                            final Long finalSkillIdToUse = skillIdToUse;
                            skillToSave = existingSecondarySkills.stream()
                                    .filter(s -> s.getId() != null && s.getId().equals(finalSkillIdToUse))
                                    .findFirst()
                                    .orElse(new com.ibm.skillspro.entity.UserSecondarySkill()); // Should ideally find
                                                                                                // an existing one
                            skillToSave.setId(skillIdToUse); // Set the parsed Long ID
                            // Reset pendingDelete to false when updating
                            skillToSave.setPendingDelete(false);
                        } else {
                            // New secondary skill: either ID is null or a temp- string
                            skillToSave = new com.ibm.skillspro.entity.UserSecondarySkill();
                            skillToSave.setId(null); // Ensure ID is null for new skills so JPA generates it
                            skillToSave.setPendingDelete(false);
                        }
                        skillToSave.setUserId(existingUser.getId());
                        skillToSave.setPractice(updatedSkillDTO.getPractice());
                        skillToSave.setPracticeArea(updatedSkillDTO.getPracticeArea());
                        skillToSave.setProductsTechnologies(updatedSkillDTO.getProductsTechnologies());
                        skillToSave.setDuration(updatedSkillDTO.getDuration());
                        skillToSave.setRoles(updatedSkillDTO.getRoles());
                        skillToSave.setCertificationLevel(updatedSkillDTO.getCertificationLevel());
                        skillToSave.setRecencyOfCertification(updatedSkillDTO.getRecencyOfCertification());
                        userService.saveUserSecondarySkill(skillToSave);
                    }

                    // Update ancillary skills
                    List<com.ibm.skillspro.entity.UserAncillarySkill> existingAncillarySkills = userService
                            .getUserAncillarySkillsByUserId(existingUser.getId());
                    List<com.ibm.skillspro.dto.UserAncillarySkillDTO> updatedAncillarySkillsDTO = updatedProfileDTO
                            .getAncillarySkills();

                    // Ensure updatedAncillarySkillsDTO is not null
                    if (updatedAncillarySkillsDTO == null) {
                        updatedAncillarySkillsDTO = java.util.Collections.emptyList();
                    }
                    System.out.println(
                            "Received updatedAncillarySkillsDTO for ancillary skills: " + updatedAncillarySkillsDTO);

                    // Identify ancillary skills to delete (present in DB but not in DTO) - SOFT DELETE
                    for (com.ibm.skillspro.entity.UserAncillarySkill existingSkill : existingAncillarySkills) {
                        boolean found = false;
                        for (com.ibm.skillspro.dto.UserAncillarySkillDTO updatedSkillDTO : updatedAncillarySkillsDTO) {
                            if (updatedSkillDTO.getId() != null && !updatedSkillDTO.getId().startsWith("temp-")) {
                                try {
                                    Long updatedSkillId = Long.parseLong(updatedSkillDTO.getId());
                                    if (existingSkill.getId() != null && existingSkill.getId().equals(updatedSkillId)) {
                                        found = true;
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println(
                                            "Warning: Non-numeric ID in DTO for existing ancillary skill during deletion check: "
                                                    + updatedSkillDTO.getId());
                                }
                            }
                        }
                        if (!found) {
                            // Soft delete: mark as pendingDelete = true
                            existingSkill.setPendingDelete(true);
                            userService.saveUserAncillarySkill(existingSkill);
                        }
                    }

                    // Add or update ancillary skills
                    for (com.ibm.skillspro.dto.UserAncillarySkillDTO updatedSkillDTO : updatedAncillarySkillsDTO) {
                        com.ibm.skillspro.entity.UserAncillarySkill skillToSave;
                        Long skillIdToUse = null; // Initialize to null

                        if (updatedSkillDTO.getId() != null && !updatedSkillDTO.getId().startsWith("temp-")) {
                            try {
                                skillIdToUse = Long.parseLong(updatedSkillDTO.getId());
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Warning: Non-numeric ID in DTO for existing ancillary skill during deletion check: "
                                                + updatedSkillDTO.getId());
                            }
                        }

                        if (skillIdToUse != null) {
                            // Update existing ancillary skill: find by parsed ID
                            final Long finalSkillIdToUse = skillIdToUse;
                            skillToSave = existingAncillarySkills.stream()
                                    .filter(s -> s.getId() != null && s.getId().equals(finalSkillIdToUse))
                                    .findFirst()
                                    .orElse(new com.ibm.skillspro.entity.UserAncillarySkill());
                            skillToSave.setId(skillIdToUse);
                            // Reset pendingDelete to false when updating
                            skillToSave.setPendingDelete(false);
                        } else {
                            // New ancillary skill: either ID is null or a temp- string
                            skillToSave = new com.ibm.skillspro.entity.UserAncillarySkill();
                            skillToSave.setId(null); // Ensure ID is null for new skills so JPA generates it
                            skillToSave.setPendingDelete(false);
                        }
                        skillToSave.setUserId(existingUser.getId());
                        skillToSave.setTechnology(updatedSkillDTO.getTechnology());
                        skillToSave.setProduct(updatedSkillDTO.getProduct());
                        skillToSave.setCertificationLink(updatedSkillDTO.getCertificationLink());
                        skillToSave.setCertified(updatedSkillDTO.isCertified());
                        System.out.println("Attempting to save ancillary skill: " + skillToSave);
                        userService.saveUserAncillarySkill(skillToSave);
                    }

                    // Update Professional Certifications
                    List<com.ibm.skillspro.entity.ProfessionalCertification> existingProfessionalCerts = userService
                            .getProfessionalCertificationsByUserId(existingUser.getId());
                    List<com.ibm.skillspro.dto.ProfessionalCertificationDTO> updatedProfessionalCertsDTO = updatedProfileDTO
                            .getProfessionalCertifications();

                    // Ensure updatedProfessionalCertsDTO is not null
                    if (updatedProfessionalCertsDTO == null) {
                        updatedProfessionalCertsDTO = java.util.Collections.emptyList();
                    }

                    // Identify professional certifications to delete (present in DB but not in DTO) - SOFT DELETE
                    for (com.ibm.skillspro.entity.ProfessionalCertification existingCert : existingProfessionalCerts) {
                        boolean found = false;
                        for (com.ibm.skillspro.dto.ProfessionalCertificationDTO updatedCertDTO : updatedProfessionalCertsDTO) {
                            if (updatedCertDTO.getId() != null) {
                                if (existingCert.getId() != null && existingCert.getId().equals(updatedCertDTO.getId())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            // Soft delete: mark as pendingDelete = true
                            existingCert.setPendingDelete(true);
                            userService.saveProfessionalCertification(existingCert);
                        }
                    }

                    // Add or update professional certifications
                    for (com.ibm.skillspro.dto.ProfessionalCertificationDTO updatedCertDTO : updatedProfessionalCertsDTO) {
                        com.ibm.skillspro.entity.ProfessionalCertification certToSave;
                        Long certIdToUse = null;

                        if (updatedCertDTO.getId() != null) {
                            certIdToUse = updatedCertDTO.getId().longValue();
                        }

                        if (certIdToUse != null) {
                            // Update existing professional certification: find by ID
                            final Long finalCertIdToUse = certIdToUse;
                            certToSave = existingProfessionalCerts.stream()
                                    .filter(c -> c.getId() != null && c.getId().equals(finalCertIdToUse))
                                    .findFirst()
                                    .orElse(new com.ibm.skillspro.entity.ProfessionalCertification());
                            certToSave.setId(finalCertIdToUse);
                            // Reset pendingDelete to false when updating
                            certToSave.setPendingDelete(false);
                        } else {
                            // New professional certification
                            certToSave = new com.ibm.skillspro.entity.ProfessionalCertification();
                            certToSave.setId(null);
                            certToSave.setPendingDelete(false);
                        }
                        certToSave.setUserId(existingUser.getId());
                        certToSave.setTitle(updatedCertDTO.getTitle());
                        certToSave.setCertified(updatedCertDTO.getCertified() != null ? updatedCertDTO.getCertified() : false);
                        certToSave.setCertificationLink(updatedCertDTO.getCertificationLink());
                        certToSave.setCertificationLevel(updatedCertDTO.getCertificationLevel());
                        userService.saveProfessionalCertification(certToSave);
                    }

                    // Update High Impact Assets
                    List<com.ibm.skillspro.entity.HighImpactAsset> existingHighImpactAssets = userService
                            .getHighImpactAssetsByUserId(existingUser.getId());
                    List<com.ibm.skillspro.dto.HighImpactAssetDTO> updatedHighImpactAssetsDTO = updatedProfileDTO
                            .getHighImpactAssets();

                    // Ensure updatedHighImpactAssetsDTO is not null
                    if (updatedHighImpactAssetsDTO == null) {
                        updatedHighImpactAssetsDTO = java.util.Collections.emptyList();
                    }

                    // Identify high impact assets to delete (present in DB but not in DTO) - SOFT DELETE
                    for (com.ibm.skillspro.entity.HighImpactAsset existingAsset : existingHighImpactAssets) {
                        boolean found = false;
                        for (com.ibm.skillspro.dto.HighImpactAssetDTO updatedAssetDTO : updatedHighImpactAssetsDTO) {
                            if (updatedAssetDTO.getId() != null) {
                                if (existingAsset.getId() != null && existingAsset.getId().equals(updatedAssetDTO.getId())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            // Soft delete: mark as pendingDelete = true
                            existingAsset.setPendingDelete(true);
                            userService.saveHighImpactAsset(existingAsset);
                        }
                    }

                    // Add or update high impact assets
                    for (com.ibm.skillspro.dto.HighImpactAssetDTO updatedAssetDTO : updatedHighImpactAssetsDTO) {
                        com.ibm.skillspro.entity.HighImpactAsset assetToSave;
                        Integer assetIdToUse = null;

                        if (updatedAssetDTO.getId() != null) {
                            assetIdToUse = updatedAssetDTO.getId();
                        }

                        if (assetIdToUse != null) {
                            // Update existing high impact asset: find by ID
                            final Integer finalAssetIdToUse = assetIdToUse;
                            assetToSave = existingHighImpactAssets.stream()
                                    .filter(a -> a.getId() != null && a.getId().equals(finalAssetIdToUse))
                                    .findFirst()
                                    .orElse(new com.ibm.skillspro.entity.HighImpactAsset());
                            assetToSave.setId(finalAssetIdToUse);
                            // Reset pendingDelete to false when updating
                            assetToSave.setPendingDelete(false);
                        } else {
                            // New high impact asset
                            assetToSave = new com.ibm.skillspro.entity.HighImpactAsset();
                            assetToSave.setId(null);
                            assetToSave.setPendingDelete(false);
                        }
                        assetToSave.setUserId(existingUser.getId().intValue());
                        assetToSave.setTitle(updatedAssetDTO.getTitle());
                        assetToSave.setBusinessImpact(updatedAssetDTO.getBusinessImpact());
                        assetToSave.setVisibilityAdoption(updatedAssetDTO.getVisibilityAdoption());
                        assetToSave.setDescription(updatedAssetDTO.getDescription());
                        assetToSave.setImpactScore(updatedAssetDTO.getImpactScore());
                        userService.saveHighImpactAsset(assetToSave);
                    }

                    // Save or update badges (user credentials)
                    List<UserCredentialDTO> updatedBadges = updatedProfileDTO.getBadges();
                    if (updatedBadges != null) {
                        for (UserCredentialDTO badgeDTO : updatedBadges) {
                            userCredentialService.saveOrUpdateUserCredential(existingUser, badgeDTO);
                        }
                    }

                    // Re-fetch the updated profile to return it
                    return userService.getUserProfileByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Profile not found after update."));
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.getUserById(id)
                .map(existingUser -> {
                    user.setId(id);
                    return ResponseEntity.ok(userService.saveUser(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // New endpoint for /profiles/{email}/profile (no /api prefix)
    @GetMapping("/profiles/{email}/profile")
    public ResponseEntity<Map<String, Object>> getUserProfileByEmailForFrontend(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("email", user.getEmail());
                    result.put("name", user.getName());
                    result.put("slackId", user.getSlackId());
                    // Fetch IBM profile for employeeType and functionalManager
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        String w3Url = "https://w3-unified-profile-api.ibm.com/v3/profiles/" + email + "/profile";
                        ResponseEntity<Map> w3Response = restTemplate.getForEntity(w3Url, Map.class);
                        Map content = (Map) w3Response.getBody().get("content");
                        Map employeeType = (Map) content.get("employeeType");
                        result.put("employeeType", employeeType);
                        
                        // Extract functionalManager data
                        Map functionalManager = (Map) content.get("functionalManager");
                        if (functionalManager != null) {
                            result.put("functionalManager", functionalManager);
                        }
                    } catch (Exception e) {
                        // If IBM profile fetch fails, set isManager: false
                        Map<String, Object> employeeType = new HashMap<>();
                        employeeType.put("isManager", false);
                        result.put("employeeType", employeeType);
                    }
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/{email}/ancillary-skill")
    public ResponseEntity<?> addAncillarySkill(@PathVariable String email, @RequestBody UserAncillarySkillDTO dto) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty())
            return ResponseEntity.notFound().build();
        User user = userOpt.get();
        UserAncillarySkill skill = new UserAncillarySkill();
        skill.setUserId(user.getId());
        skill.setTechnology(dto.getTechnology());
        skill.setProduct(dto.getProduct());
        skill.setCertified(dto.isCertified());
        skill.setCertificationLink(dto.getCertificationLink());
        skill.setCertificationLevel(dto.getCertificationLevel());
        skill.setRecencyOfCertification(dto.getRecencyOfCertification());
        userAncillarySkillRepository.save(skill);
        return ResponseEntity.ok().build();
    }

    // Proxy endpoint for IBM profile image
    @GetMapping("/api/proxy/ibm-profile-image/{email}")
    public ResponseEntity<byte[]> proxyIbmProfileImage(
            @PathVariable(name = "email", required = false) String emailEncoded,
            @RequestParam(value = "s", required = false, defaultValue = "0") String s,
            @RequestParam(value = "size", required = false, defaultValue = "0") String size) {
        // Decode the email in case it is URL-encoded (e.g., %40 for @)
        String email = emailEncoded != null ? URLDecoder.decode(emailEncoded, StandardCharsets.UTF_8) : "";
        String url = "https://w3-unified-profile-api.ibm.com/v3/image/" + email + "?s=" + s + "&size=" + size;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "*/*");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.IMAGE_JPEG);
            responseHeaders.setCacheControl("no-transform,max-age=86400,no-cache=set-cookie");
            responseHeaders.set("Access-Control-Allow-Origin", "*");

            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
