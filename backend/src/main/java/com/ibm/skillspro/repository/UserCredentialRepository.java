package com.ibm.skillspro.repository;

import com.ibm.skillspro.entity.UserCredential;
import com.ibm.skillspro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    List<UserCredential> findByUser(User user);
} 