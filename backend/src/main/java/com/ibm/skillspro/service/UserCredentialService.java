package com.ibm.skillspro.service;

import com.ibm.skillspro.dto.UserCredentialDTO;
import com.ibm.skillspro.entity.User;
import com.ibm.skillspro.entity.UserCredential;
import com.ibm.skillspro.repository.UserCredentialRepository;
import com.ibm.skillspro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCredentialService {
    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private UserRepository userRepository;

    public List<UserCredentialDTO> getUserCredentialsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        List<UserCredential> credentials = userCredentialRepository.findByUser(user);
        return credentials.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<UserCredentialDTO> getUserCredentialsByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) return List.of();
        List<UserCredential> credentials = userCredentialRepository.findByUser(user);
        return credentials.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void saveOrUpdateUserCredential(User user, UserCredentialDTO badgeDTO) {
        if (user == null || badgeDTO == null) return;
        UserCredential credential = null;
        if (badgeDTO.getCredentialOrderId() != null) {
            List<UserCredential> existing = userCredentialRepository.findByUser(user).stream()
                .filter(c -> badgeDTO.getCredentialOrderId().equals(c.getCredentialOrderId()))
                .collect(Collectors.toList());
            if (!existing.isEmpty()) {
                credential = existing.get(0);
            }
        }
        if (credential == null) {
            credential = new UserCredential();
            credential.setUser(user);
        }
        credential.setCredentialType(badgeDTO.getCredentialType());
        credential.setEmployeeId(badgeDTO.getEmployeeId());
        credential.setLearningSource(badgeDTO.getLearningSource());
        credential.setCredentialStatus(badgeDTO.getCredentialStatus());
        credential.setCredentialOrderId(badgeDTO.getCredentialOrderId());
        credential.setDigitalCredentialId(badgeDTO.getDigitalCredentialId());
        credential.setCredentialLabel(badgeDTO.getCredentialLabel());
        try {
            if (badgeDTO.getCredentialDate() != null) {
                credential.setCredentialDate(java.sql.Date.valueOf(badgeDTO.getCredentialDate().substring(0, 10)));
            }
            if (badgeDTO.getCredentialExpiryDate() != null) {
                credential.setCredentialExpiryDate(java.sql.Date.valueOf(badgeDTO.getCredentialExpiryDate().substring(0, 10)));
            }
        } catch (Exception e) {
            // Ignore date parse errors
        }
        userCredentialRepository.save(credential);
    }

    private UserCredentialDTO toDTO(UserCredential credential) {
        UserCredentialDTO dto = new UserCredentialDTO();
        dto.setCredentialType(credential.getCredentialType());
        dto.setEmployeeId(credential.getEmployeeId());
        dto.setLearningSource(credential.getLearningSource());
        dto.setCredentialStatus(credential.getCredentialStatus());
        dto.setCredentialOrderId(credential.getCredentialOrderId());
        dto.setDigitalCredentialId(credential.getDigitalCredentialId());
        dto.setCredentialLabel(credential.getCredentialLabel());
        // Set credentialTitle as credentialLabel (fallback)
        if (credential.getCredentialLabel() != null) {
            dto.setCredentialTitle(credential.getCredentialLabel());
        } else {
            dto.setCredentialTitle(null);
        }
        if (credential.getCredentialDate() != null) {
            dto.setCredentialDate(credential.getCredentialDate().toString());
        }
        if (credential.getCredentialExpiryDate() != null) {
            dto.setCredentialExpiryDate(credential.getCredentialExpiryDate().toString());
        }
        return dto;
    }
} 