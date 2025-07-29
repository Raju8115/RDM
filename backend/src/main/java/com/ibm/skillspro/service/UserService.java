package com.ibm.skillspro.service;

import com.ibm.skillspro.entity.User;
import com.ibm.skillspro.entity.UserSkill;
import com.ibm.skillspro.entity.UserSecondarySkill;
import com.ibm.skillspro.entity.UserAncillarySkill;
import com.ibm.skillspro.entity.UserSkillInfo;
import com.ibm.skillspro.repository.UserRepository;
import com.ibm.skillspro.repository.UserSkillRepository;
import com.ibm.skillspro.repository.UserSecondarySkillRepository;
import com.ibm.skillspro.repository.UserAncillarySkillRepository;
import com.ibm.skillspro.repository.UserSkillInfoRepository;
import com.ibm.skillspro.dto.UserProfileDTO;
import com.ibm.skillspro.dto.UserSkillDTO;
import com.ibm.skillspro.dto.UserSecondarySkillDTO;
import com.ibm.skillspro.dto.UserAncillarySkillDTO;
import com.ibm.skillspro.dto.UserSkillInfoDTO;
import com.ibm.skillspro.service.UserCredentialService;
import com.ibm.skillspro.dto.UserCredentialDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.ibm.skillspro.dto.ProfessionalCertificationDTO;
import com.ibm.skillspro.entity.ProfessionalCertification;
import com.ibm.skillspro.service.ProfessionalCertificationService;
import com.ibm.skillspro.entity.HighImpactAsset;
import com.ibm.skillspro.service.HighImpactAssetService;
import com.ibm.skillspro.dto.HighImpactAssetDTO;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserSecondarySkillRepository userSecondarySkillRepository;

    @Autowired
    private UserAncillarySkillRepository userAncillarySkillRepository;

    @Autowired
    private UserSkillInfoRepository userSkillInfoRepository;

    @Autowired
    private UserCredentialService userCredentialService;

    @Autowired
    private ProfessionalCertificationService professionalCertificationService;

    @Autowired
    private HighImpactAssetService highImpactAssetService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<UserSkill> getUserSkillsByUserId(Long userId) {
        return userSkillRepository.findByUserId(userId);
    }

    public UserSkill saveUserSkill(UserSkill userSkill) {
        return userSkillRepository.save(userSkill);
    }

    public UserSkillInfo saveUserSkillInfo(UserSkillInfo userSkillInfo) {
        return userSkillInfoRepository.save(userSkillInfo);
    }

    public List<UserSkillInfo> getUserSkillInfoByUserId(Long userId) {
        // Fetch the user's primary skill
        List<UserSkill> primarySkills = getUserSkillsByUserId(userId);
        if (!primarySkills.isEmpty()) {
            Long primarySkillId = primarySkills.get(0).getId();
            // Fetch project experiences for this user and their primary skill, excluding soft-deleted items
            return userSkillInfoRepository.findByUserIdAndUserSkillId(userId, primarySkillId)
                    .stream()
                    .filter(project -> !project.isPendingDelete())
                    .collect(Collectors.toList());
        } else {
            // Fallback: fetch by userId only (should be empty for new users), excluding soft-deleted items
            return userSkillInfoRepository.findByUserId(userId)
                    .stream()
                    .filter(project -> !project.isPendingDelete())
                    .collect(Collectors.toList());
        }
    }

    public void deleteUserSkillInfo(Long id) {
        userSkillInfoRepository.deleteById(id);
    }

    public List<UserSecondarySkill> getUserSecondarySkillsByUserId(Long userId) {
        return userSecondarySkillRepository.findByUserId(userId)
                .stream()
                .filter(skill -> !skill.isPendingDelete())
                .collect(Collectors.toList());
    }

    public UserSecondarySkill saveUserSecondarySkill(UserSecondarySkill userSecondarySkill) {
        return userSecondarySkillRepository.save(userSecondarySkill);
    }

    public void deleteUserSecondarySkill(Long id) {
        userSecondarySkillRepository.deleteById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<UserProfileDTO> getUserProfileByEmail(String email, boolean isNewUser) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> {
                    UserProfileDTO dto = new UserProfileDTO();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setSlackId(user.getSlackId());
                    dto.setIsNewUser(isNewUser);

                    // Get primary skills
                    List<UserSkill> primarySkills = getUserSkillsByUserId(user.getId());
                    if (!primarySkills.isEmpty()) {
                        UserSkill mainSkill = primarySkills.get(0);
                        dto.setPracticeId(mainSkill.getPracticeId());
                        dto.setPracticeAreaId(mainSkill.getPracticeAreaId());
                        dto.setPracticeProductTechnologyId(mainSkill.getPracticeProductTechnologyId());
                        dto.setProjectsDone(mainSkill.getProjectsDone());
                        dto.setSelfAssessmentLevel(mainSkill.getSelfAssessmentLevel());
                        dto.setProfessionalLevel(mainSkill.getProfessionalLevel());
                    }

                    // Map primary skills
                    dto.setPrimarySkills(primarySkills.stream()
                            .map(this::mapToUserSkillDTO)
                            .collect(Collectors.toList()));

                    // Map secondary skills
                    List<UserSecondarySkill> secondarySkills = getUserSecondarySkillsByUserId(user.getId());
                    dto.setSecondarySkills(secondarySkills.stream()
                            .map(this::mapToUserSecondarySkillDTO)
                            .collect(Collectors.toList()));

                    // Map ancillary skills
                    List<UserAncillarySkill> ancillarySkills = getUserAncillarySkillsByUserId(user.getId());
                    dto.setAncillarySkills(ancillarySkills.stream()
                            .map(this::mapToUserAncillarySkillDTO)
                            .collect(Collectors.toList()));

                    // Map project experiences
                    List<UserSkillInfo> projectExperiences = getUserSkillInfoByUserId(user.getId());
                    dto.setProjectExperiences(projectExperiences.stream()
                            .map(this::mapToUserSkillInfoDTO)
                            .collect(Collectors.toList()));

                    // Map badges (user credentials)
                    List<UserCredentialDTO> badges = userCredentialService.getUserCredentialsByUserId(user.getId());
                    dto.setBadges(badges);

                    // Map professional certifications
                    List<ProfessionalCertificationDTO> professionalCertifications = professionalCertificationService.getCertificationsByUserId(user.getId()).stream()
                        .map(cert -> {
                            ProfessionalCertificationDTO certDTO = new ProfessionalCertificationDTO();
                            certDTO.setId(cert.getId());
                            certDTO.setUserId(cert.getUserId());
                            certDTO.setTitle(cert.getTitle());
                            certDTO.setCertified(cert.isCertified());
                            certDTO.setCertificationLink(cert.getCertificationLink());
                            certDTO.setCertificationLevel(cert.getCertificationLevel());
                            certDTO.setCertificationScore(cert.getCertificationScore());
                            return certDTO;
                        })
                        .collect(Collectors.toList());
                    dto.setProfessionalCertifications(professionalCertifications);

                    // Map high impact assets
                    List<HighImpactAsset> highImpactAssets = highImpactAssetService.getAssetsByUserId(user.getId().intValue());
                    dto.setHighImpactAssets(highImpactAssets.stream()
                        .map(this::mapToHighImpactAssetDTO)
                        .collect(Collectors.toList()));

                    return dto;
                });
    }

    // Overload for backward compatibility
    public Optional<UserProfileDTO> getUserProfileByEmail(String email) {
        return getUserProfileByEmail(email, false);
    }

    private UserSkillDTO mapToUserSkillDTO(UserSkill userSkill) {
        UserSkillDTO dto = new UserSkillDTO();
        dto.setId(userSkill.getId());
        dto.setProjectsDone(userSkill.getProjectsDone());
        dto.setSelfAssessmentLevel(userSkill.getSelfAssessmentLevel());
        dto.setProfessionalLevel(userSkill.getProfessionalLevel());
        // Note: You'll need to fetch practice, practice area, and product names from
        // their respective repositories
        return dto;
    }

    private UserSecondarySkillDTO mapToUserSecondarySkillDTO(UserSecondarySkill skill) {
        UserSecondarySkillDTO dto = new UserSecondarySkillDTO();
        dto.setId(String.valueOf(skill.getId()));
        dto.setPractice(skill.getPractice());
        dto.setPracticeArea(skill.getPracticeArea());
        dto.setProductsTechnologies(skill.getProductsTechnologies());
        dto.setDuration(skill.getDuration());
        dto.setRoles(skill.getRoles());
        dto.setCertificationLevel(skill.getCertificationLevel());
        dto.setRecencyOfCertification(skill.getRecencyOfCertification());
        double score = UserSecondarySkill.calculateCertificationScore(
                skill.getCertificationLevel(),
                skill.getRecencyOfCertification());
        dto.setCertificationScore(score);
        return dto;
    }

    // Add a method to map from DTO to entity for saving
    public UserSecondarySkill mapToUserSecondarySkillEntity(UserSecondarySkillDTO dto, UserSecondarySkill entity) {
        entity.setPractice(dto.getPractice());
        entity.setPracticeArea(dto.getPracticeArea());
        entity.setProductsTechnologies(dto.getProductsTechnologies());
        entity.setDuration(dto.getDuration());
        entity.setRoles(dto.getRoles());
        entity.setCertificationLevel(dto.getCertificationLevel());
        entity.setRecencyOfCertification(dto.getRecencyOfCertification());
        return entity;
    }

    private UserAncillarySkillDTO mapToUserAncillarySkillDTO(UserAncillarySkill skill) {
        UserAncillarySkillDTO dto = new UserAncillarySkillDTO();
        dto.setId(String.valueOf(skill.getId()));
        dto.setTechnology(skill.getTechnology());
        dto.setProduct(skill.getProduct());
        dto.setCertified(skill.isCertified());
        dto.setCertificationLink(skill.getCertificationLink());
        dto.setCertificationLevel(skill.getCertificationLevel());
        dto.setRecencyOfCertification(skill.getRecencyOfCertification());
        double score = UserAncillarySkill.calculateCertificationScore(
                skill.getCertificationLevel(),
                skill.getRecencyOfCertification());
        dto.setCertificationScore(score);
        return dto;
    }

    // Add a method to map from DTO to entity for saving
    public UserAncillarySkill mapToUserAncillarySkillEntity(UserAncillarySkillDTO dto, UserAncillarySkill entity) {
        entity.setTechnology(dto.getTechnology());
        entity.setProduct(dto.getProduct());
        entity.setCertified(dto.isCertified());
        entity.setCertificationLink(dto.getCertificationLink());
        entity.setCertificationLevel(dto.getCertificationLevel());
        entity.setRecencyOfCertification(dto.getRecencyOfCertification());
        return entity;
    }

    private UserSkillInfoDTO mapToUserSkillInfoDTO(UserSkillInfo userSkillInfo) {
        UserSkillInfoDTO dto = new UserSkillInfoDTO();
        dto.setId(String.valueOf(userSkillInfo.getId()));
        dto.setProjectTitle(userSkillInfo.getProjectTitle());
        dto.setTechnologiesUsed(userSkillInfo.getTechnologiesUsed());
        dto.setDuration(userSkillInfo.getDuration());
        dto.setResponsibilities(userSkillInfo.getResponsibilities());
        dto.setClientTier(userSkillInfo.getClientTier());
        dto.setClientTierV2(userSkillInfo.getClientTierV2());
        dto.setProjectComplexity(userSkillInfo.getProjectComplexity());
        double score = UserSkillInfo.calculateProjectScore(
                userSkillInfo.getProjectComplexity(),
                userSkillInfo.getResponsibilities(),
                userSkillInfo.getDuration(),
                userSkillInfo.getClientTierV2());
        dto.setProjectScore(score);
        return dto;
    }

    // Add a method to map from DTO to entity for saving
    public UserSkillInfo mapToUserSkillInfoEntity(UserSkillInfoDTO dto, UserSkillInfo entity) {
        entity.setProjectTitle(dto.getProjectTitle());
        entity.setTechnologiesUsed(dto.getTechnologiesUsed());
        entity.setDuration(dto.getDuration());
        entity.setResponsibilities(dto.getResponsibilities());
        entity.setClientTier(dto.getClientTier());
        entity.setClientTierV2(dto.getClientTierV2());
        entity.setProjectComplexity(dto.getProjectComplexity());
        return entity;
    }

    public List<UserAncillarySkill> getUserAncillarySkillsByUserId(Long userId) {
        return userAncillarySkillRepository.findByUserId(userId)
                .stream()
                .filter(skill -> !skill.isPendingDelete())
                .collect(Collectors.toList());
    }

    public UserAncillarySkill saveUserAncillarySkill(UserAncillarySkill skill) {
        return userAncillarySkillRepository.save(skill);
    }

    public void deleteUserAncillarySkill(Long id) {
        userAncillarySkillRepository.deleteById(id);
    }

    // High Impact Asset methods
    public List<HighImpactAsset> getHighImpactAssetsByUserId(Long userId) {
        return highImpactAssetService.getAssetsByUserId(userId.intValue());
    }

    public HighImpactAsset saveHighImpactAsset(HighImpactAsset asset) {
        return highImpactAssetService.addAsset(asset);
    }

    // Professional Certification methods
    public List<ProfessionalCertification> getProfessionalCertificationsByUserId(Long userId) {
        return professionalCertificationService.getCertificationsByUserId(userId);
    }

    public ProfessionalCertification saveProfessionalCertification(ProfessionalCertification cert) {
        return professionalCertificationService.addCertification(cert);
    }

    private HighImpactAssetDTO mapToHighImpactAssetDTO(HighImpactAsset asset) {
        HighImpactAssetDTO dto = new HighImpactAssetDTO();
        dto.setId(asset.getId());
        dto.setUserId(asset.getUserId());
        dto.setTitle(asset.getTitle());
        dto.setBusinessImpact(asset.getBusinessImpact());
        dto.setVisibilityAdoption(asset.getVisibilityAdoption());
        dto.setDescription(asset.getDescription());
        dto.setImpactScore(asset.getImpactScore());
        dto.setPendingDelete(asset.isPendingDelete());
        return dto;
    }

    // Methods to permanently delete items marked for deletion (called after manager approval)
    public void deletePendingUserSkillInfo(Long userId) {
        List<UserSkillInfo> pendingItems = userSkillInfoRepository.findByUserId(userId).stream()
            .filter(item -> item.isPendingDelete())
            .collect(Collectors.toList());
        userSkillInfoRepository.deleteAll(pendingItems);
    }

    public void deletePendingUserSecondarySkills(Long userId) {
        List<UserSecondarySkill> pendingItems = userSecondarySkillRepository.findByUserId(userId).stream()
            .filter(item -> item.isPendingDelete())
            .collect(Collectors.toList());
        userSecondarySkillRepository.deleteAll(pendingItems);
    }

    public void deletePendingUserAncillarySkills(Long userId) {
        List<UserAncillarySkill> pendingItems = userAncillarySkillRepository.findByUserId(userId).stream()
            .filter(item -> item.isPendingDelete())
            .collect(Collectors.toList());
        userAncillarySkillRepository.deleteAll(pendingItems);
    }

    public void deletePendingHighImpactAssets(Long userId) {
        List<HighImpactAsset> pendingItems = highImpactAssetService.getAssetsByUserId(userId.intValue()).stream()
            .filter(item -> item.isPendingDelete())
            .collect(Collectors.toList());
        for (HighImpactAsset asset : pendingItems) {
            highImpactAssetService.deleteAsset(asset.getId());
        }
    }

    public void deletePendingProfessionalCertifications(Long userId) {
        List<ProfessionalCertification> pendingItems = professionalCertificationService.getCertificationsByUserId(userId).stream()
            .filter(item -> item.isPendingDelete())
            .collect(Collectors.toList());
        for (ProfessionalCertification cert : pendingItems) {
            professionalCertificationService.deleteCertification(cert.getId());
        }
    }

    // Method to delete all pending items for a user (called when manager approves)
    public void deleteAllPendingItems(Long userId) {
        deletePendingUserSkillInfo(userId);
        deletePendingUserSecondarySkills(userId);
        deletePendingUserAncillarySkills(userId);
        deletePendingHighImpactAssets(userId);
        deletePendingProfessionalCertifications(userId);
    }
}