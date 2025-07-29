package com.ibm.skillspro.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_credentials")
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "credential_order_id")
    private String credentialOrderId;

    @Column(name = "credential_date")
    private Date credentialDate;

    @Column(name = "digital_credential_id")
    private String digitalCredentialId;

    @Column(name = "credential_label", length = 512)
    private String credentialLabel;

    @Column(name = "credential_type")
    private String credentialType;

    @Column(name = "learning_source")
    private String learningSource;

    @Column(name = "credential_expiry_date")
    private Date credentialExpiryDate;

    @Column(name = "credential_status")
    private String credentialStatus;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCredentialOrderId() { return credentialOrderId; }
    public void setCredentialOrderId(String credentialOrderId) { this.credentialOrderId = credentialOrderId; }
    public Date getCredentialDate() { return credentialDate; }
    public void setCredentialDate(Date credentialDate) { this.credentialDate = credentialDate; }
    public String getDigitalCredentialId() { return digitalCredentialId; }
    public void setDigitalCredentialId(String digitalCredentialId) { this.digitalCredentialId = digitalCredentialId; }
    public String getCredentialLabel() { return credentialLabel; }
    public void setCredentialLabel(String credentialLabel) { this.credentialLabel = credentialLabel; }
    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }
    public String getLearningSource() { return learningSource; }
    public void setLearningSource(String learningSource) { this.learningSource = learningSource; }
    public Date getCredentialExpiryDate() { return credentialExpiryDate; }
    public void setCredentialExpiryDate(Date credentialExpiryDate) { this.credentialExpiryDate = credentialExpiryDate; }
    public String getCredentialStatus() { return credentialStatus; }
    public void setCredentialStatus(String credentialStatus) { this.credentialStatus = credentialStatus; }
} 