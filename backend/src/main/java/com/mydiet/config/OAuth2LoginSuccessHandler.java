package com.mydiet.config;

import com.mydiet.service.OAuth2UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        OAuth2UserPrincipal oAuth2User = (OAuth2UserPrincipal) authentication.getPrincipal();
        
        log.debug("OAuth2 로그인 성공: {}", oAuth2User.getName());
        log.debug("사용자 이메일: {}", oAuth2User.getEmail());
        log.debug("사용자 닉네임: {}", oAuth2User.getNickname());
        
        if (oAuth2User.getUser() != null) {
            getRedirectStrategy().sendRedirect(request, response, "/dashboard.html");
        } else {
            log.error("OAuth2 사용자 정보 저장 실패");
            getRedirectStrategy().sendRedirect(request, response, "/auth.html?error=save_failed");
        }
    }
}