package com.ibm.skillspro.controller;

import com.ibm.skillspro.dto.UserCredentialDTO;
import com.ibm.skillspro.service.UserCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-credentials")
public class UserCredentialController {
    @Autowired
    private UserCredentialService userCredentialService;

    @GetMapping("/user/{userId}")
    public List<UserCredentialDTO> getUserCredentials(@PathVariable Long userId) {
        return userCredentialService.getUserCredentialsByUserId(userId);
    }

    @GetMapping("/by-email/{email}")
    public List<UserCredentialDTO> getUserCredentialsByEmail(@PathVariable String email) {
        return userCredentialService.getUserCredentialsByEmail(email);
    }
} 