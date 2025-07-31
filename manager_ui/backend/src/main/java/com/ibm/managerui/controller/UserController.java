package com.ibm.managerui.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://rdm-frontend-2-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user")
    public Object getUser(Authentication authentication) {
        if (authentication == null) return Map.of("error", "Not authenticated");
        Object principal = authentication.getPrincipal();
        String email = null;
        String name = null;
        String slackId = null;
        if (principal instanceof OidcUser oidcUser) {
            email = (String) oidcUser.getClaims().get("email");
            name = (String) oidcUser.getClaims().getOrDefault("name", "");
            slackId = (String) oidcUser.getClaims().getOrDefault("preferredSlackUsername", "");
        }
        if (email == null) return Map.of("error", "No email found");
        return Map.of(
            "email", email,
            "name", name,
            "slackId", slackId
        );
    }

    @GetMapping("/badges/by-email/{email}")
    public ResponseEntity<Object> getBadgesByEmail(@PathVariable String email) {
        String url = "https://rdm-backend-1-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/api/user-credentials/by-email/" + email;
        RestTemplate restTemplate = new RestTemplate();
        try {
            logger.info("Fetching badges for email: {} from {}", email, url);
            Object badges = restTemplate.getForObject(url, Object.class);
            logger.info("Badges response for {}: {}", email, badges);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            logger.error("Failed to fetch badges for {}: {}", email, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch badges", "details", e.getMessage()));
        }
    }
} 
