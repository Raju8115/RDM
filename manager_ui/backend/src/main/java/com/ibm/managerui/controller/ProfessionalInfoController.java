package com.ibm.managerui.controller;

import com.ibm.managerui.dto.ProfessionalInfoDTO;
import com.ibm.managerui.service.ProfessionalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/professional-info")
public class ProfessionalInfoController {
    @Autowired
    private ProfessionalInfoService service;

    @GetMapping("/email/{email}")
    public Optional<ProfessionalInfoDTO> getProfileByEmail(@PathVariable String email) {
        return service.getProfileByEmail(email);
    }
} 