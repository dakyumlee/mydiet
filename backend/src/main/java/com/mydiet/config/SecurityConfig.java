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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/auth.html", "/login", "/css/**", "/js/**", "/static/**", "/api/public/**")
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth.html")
                .defaultSuccessUrl("/dashboard.html", true)
                .failureUrl("/auth.html?error=true")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService)
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/auth.html")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            );

        return http.build();
    }
}