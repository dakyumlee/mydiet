package com.mydiet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (개발 단계)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/dashboard.html",
                    "/admin-login.html",
                    "/admin-dashboard.html",
                    "/meal-management.html",
                    "/workout-management.html",
                    "/emotion-diary.html",
                    "/profile-settings.html",
                    "/analytics.html",
                    "/auth.html",
                    "/api/**",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // X-Frame-Options 설정
            );

        return http.build();
    }
}