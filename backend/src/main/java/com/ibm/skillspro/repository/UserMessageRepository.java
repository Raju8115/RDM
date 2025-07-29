package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
    List<UserMessage> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<UserMessage> findByUserEmailAndReadOrderByCreatedAtDesc(String userEmail, Boolean read);
    long countByUserEmailAndRead(String userEmail, Boolean read);
    List<UserMessage> findByUserEmailIgnoreCaseOrderByCreatedAtDesc(String userEmail);
    List<UserMessage> findByUserEmailIgnoreCaseAndReadOrderByCreatedAtDesc(String userEmail, Boolean read);
} 