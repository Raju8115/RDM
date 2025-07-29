package com.ibm.skillspro.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Invalidate session
        request.logout();
        request.getSession().invalidate();

        // Remove cookies (optional, for frontend cookies if any)
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        // Redirect to App ID logout
        response.sendRedirect("https://au-syd.appid.cloud.ibm.com/logout?federated&post_logout_redirect_uri=http://localhost:3000");
    }

}
