package com.mydiet.config;

import com.mydiet.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/", "/index.html", "/auth.html", "/admin-login.html",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico",
                    "/oauth2/**", "/login/**", "/error", "/static/**"
                ).permitAll()
                
                .requestMatchers("/api/simple/**", "/api/debug/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                .requestMatchers("/admin-dashboard.html").hasRole("ADMIN")
                
                .requestMatchers("/dashboard.html", "/meal-management.html", 
                                "/workout-management.html", "/emotion-diary.html",
                                "/profile-settings.html", "/analytics.html").authenticated()
                
                .requestMatchers("/api/**").authenticated()
                
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth.html")
                .userInfoEndpoint(userInfo -> 
                    userInfo.userService(oAuth2UserService)
                )
                .successHandler(successHandler)
                .failureUrl("/auth.html?error=oauth_failed")
                .defaultSuccessUrl("/dashboard.html", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}