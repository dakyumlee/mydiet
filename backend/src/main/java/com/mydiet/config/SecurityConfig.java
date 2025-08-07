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
    private final SessionAuthenticationFilter sessionAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(sessionAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authz -> authz
                // 🔥 메인 페이지와 정적 리소스 완전 허용
                .requestMatchers(
                    "/", "/index.html", "/auth.html", "/admin-login.html",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico",
                    "/oauth2/**", "/login/**", "/error", "/static/**"
                ).permitAll()
                
                // 🔥 API 엔드포인트
                .requestMatchers("/api/simple/**", "/api/debug/**", "/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").authenticated()  // hasRole 제거
                
                // 🔥 관리자 페이지는 세션 기반으로 확인
                .requestMatchers("/admin-dashboard.html").authenticated()
                
                // 🔥 일반 대시보드는 인증만 필요
                .requestMatchers("/dashboard.html", "/meal-management.html", 
                                "/workout-management.html", "/emotion-diary.html",
                                "/profile-settings.html", "/analytics.html").authenticated()
                
                // 🔥 API는 인증 필요
                .requestMatchers("/api/**").authenticated()
                
                // 🔥 나머지는 모두 허용 (중요!)
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth.html")
                .userInfoEndpoint(userInfo -> 
                    userInfo.userService(oAuth2UserService)
                )
                .successHandler(successHandler)
                .failureUrl("/auth.html?error=oauth_failed")
                .defaultSuccessUrl("/dashboard.html", true)  // 강제 리다이렉트
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