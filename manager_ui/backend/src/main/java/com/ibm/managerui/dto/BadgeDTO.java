package com.ibm.managerui.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeDTO {
    private Long id;
    private String credentialTitle;
    private String credentialType;
    private String learningSource;
    private String credentialStatus;
    private String credentialDate;
    private String credentialExpiryDate;
} 