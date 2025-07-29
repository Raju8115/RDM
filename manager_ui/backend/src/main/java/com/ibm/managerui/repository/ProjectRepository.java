package com.ibm.managerui.repository;

import com.ibm.managerui.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface ProjectRepository extends JpaRepository<Project, Long> {
} 