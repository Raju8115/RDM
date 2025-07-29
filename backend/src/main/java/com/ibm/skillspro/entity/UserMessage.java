package com.ibm.skillspro.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_messages")
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "message_type", nullable = false)
    private String messageType = "REJECTION"; // Default to rejection

    @Column(name = "read_status", nullable = false)
    private Boolean read = false;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "manager_email")
    private String managerEmail;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
} 