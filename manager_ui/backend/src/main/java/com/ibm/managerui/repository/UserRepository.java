package com.ibm.managerui.repository;

import com.ibm.managerui.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface UserRepository extends JpaRepository<User, Long> {
} 