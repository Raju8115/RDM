package com.ibm.managerui.service;

import com.ibm.managerui.dto.ProfessionalInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Service
public class ProfessionalInfoService {
    private static final Logger logger = LoggerFactory.getLogger(ProfessionalInfoService.class);
    private final String USER_PROFILE_API = "https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/api/profile/";

    @Autowired
    private RestTemplate restTemplate;

    public Optional<ProfessionalInfoDTO> getProfileByEmail(String email) {
        try {
            logger.info("Fetching profile for email: {}", email);
            // Fetch as Map
            Map profileMap = restTemplate.getForObject(USER_PROFILE_API + email, Map.class);
            logger.info("Fetched profile map: {}", profileMap);
            // Fetch practices for mapping
            Object practicesObj = restTemplate.getForObject("http://localhost:8082/api/practices", Object.class);
            java.util.List<?> practices = (java.util.List<?>) practicesObj;
            // Helper lambdas for mapping
            java.util.function.Function<Object, String> getPracticeName = (id) -> {
                if (id == null) return null;
                for (Object p : practices) {
                    java.util.Map<?, ?> pm = (java.util.Map<?, ?>) p;
                    if (String.valueOf(pm.get("id")).equals(String.valueOf(id))) {
                        return (String) pm.get("name");
                    }
                }
                return String.valueOf(id);
            };
            java.util.function.Function<Object, String> getPracticeAreaName = (id) -> {
                if (id == null) return null;
                for (Object p : practices) {
                    java.util.Map<?, ?> pm = (java.util.Map<?, ?>) p;
                    java.util.List<?> areas = (java.util.List<?>) pm.get("practiceAreas");
                    if (areas != null) {
                        for (Object a : areas) {
                            java.util.Map<?, ?> am = (java.util.Map<?, ?>) a;
                            if (String.valueOf(am.get("id")).equals(String.valueOf(id))) {
                                return (String) am.get("name");
                            }
                        }
                    }
                }
                return String.valueOf(id);
            };
            java.util.function.Function<Object, String> getProductName = (id) -> {
                if (id == null) return null;
                for (Object p : practices) {
                    java.util.Map<?, ?> pm = (java.util.Map<?, ?>) p;
                    java.util.List<?> areas = (java.util.List<?>) pm.get("practiceAreas");
                    if (areas != null) {
                        for (Object a : areas) {
                            java.util.Map<?, ?> am = (java.util.Map<?, ?>) a;
                            java.util.List<?> products = (java.util.List<?>) am.get("products");
                            if (products != null) {
                                for (Object prod : products) {
                                    java.util.Map<?, ?> prodm = (java.util.Map<?, ?>) prod;
                                    if (String.valueOf(prodm.get("id")).equals(String.valueOf(id))) {
                                        return (String) prodm.get("product_name");
                                    }
                                }
                            }
                        }
                    }
                }
                return String.valueOf(id);
            };
            // Extract top-level fields
            Object practiceId = profileMap.get("practiceId");
            Object practiceAreaId = profileMap.get("practiceAreaId");
            Object practiceProductId = profileMap.get("practiceProductTechnologyId");
            String customerProjects = (String) profileMap.get("projectsDone");
            String selfAssessment = (String) profileMap.get("selfAssessmentLevel");
            String professionalLevel = (String) profileMap.get("professionalLevel");
            // Build DTO
            ProfessionalInfoDTO dto = new ProfessionalInfoDTO();
            dto.setName((String) profileMap.get("name"));
            dto.setEmail((String) profileMap.get("email"));
            dto.setSlackId((String) profileMap.get("slackId"));
            dto.setPractice(getPracticeName.apply(practiceId));
            dto.setPracticeArea(getPracticeAreaName.apply(practiceAreaId));
            dto.setPracticeProduct(getProductName.apply(practiceProductId));
            dto.setCustomerProjects(customerProjects);
            dto.setSelfAssessment(selfAssessment);
            dto.setProfessionalLevel(professionalLevel);
            // Set other sections if needed (projectExperiences, secondarySkills, etc.)
            dto.setProjectExperiences((java.util.List) profileMap.get("projectExperiences"));
            dto.setSecondarySkills((java.util.List) profileMap.get("secondarySkills"));
            dto.setAncillarySkills((java.util.List) profileMap.get("ancillarySkills"));
            dto.setProfessionalCertifications((java.util.List) profileMap.get("professionalCertifications"));
            dto.setBadges((java.util.List) profileMap.get("badges"));
            dto.setHighImpactAssets((java.util.List) profileMap.get("highImpactAssets"));
            return Optional.of(dto);
        } catch (Exception e) {
            logger.error("Error fetching profile for email: {}", email, e);
            return Optional.empty();
        }
    }
} 
