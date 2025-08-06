package com.mydiet.config;

import com.mydiet.model.User;
import com.mydiet.service.OAuth2UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        
        OAuth2UserPrincipal userPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();
        
        // 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userNickname", user.getNickname());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("isAuthenticated", true);
        
        if ("ADMIN".equals(user.getRole())) {
            session.setAttribute("isAdmin", true);
        }
        
        log.info("OAuth2 로그인 성공 - 사용자 ID: {}, 닉네임: {}, 역할: {}", 
                user.getId(), user.getNickname(), user.getRole());
        
        // 역할에 따라 리다이렉트
        if ("ADMIN".equals(user.getRole())) {
            getRedirectStrategy().sendRedirect(request, response, "/admin-dashboard.html");
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/dashboard.html");
        }
    }
}