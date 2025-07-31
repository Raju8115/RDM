package com.ibm.skillspro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login**/", "/api/*/", "api/userinfo/").authenticated()
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // Enable CORS
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("https://rdm-frontend-2-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com/personal-info", true) // <--- Redirect to React frontend
                )
                .logout(logout -> logout.logoutSuccessUrl("https://rdm-frontend-2-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com"));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "https://rdm-frontend-2-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com")); // Allow both React apps
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true); // Allow cookies (e.g. JSESSIONID)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

