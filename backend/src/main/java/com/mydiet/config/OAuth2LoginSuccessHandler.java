package com.mydiet.config;

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

@Slf4j
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        log.info("=== OAuth 로그인 성공 핸들러 시작 ===");
        
        try {
            OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
            
            log.info("🎉 OAuth 로그인 성공!");
            log.info("📧 이메일: {}", principal.getEmail());
            log.info("👤 닉네임: {}", principal.getNickname());
            log.info("🔑 역할: {}", principal.getRole());
            log.info("🆔 사용자 ID: {}", principal.getUserId());
            
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", principal.getUserId());
            session.setAttribute("userEmail", principal.getEmail());
            session.setAttribute("userNickname", principal.getNickname());
            session.setAttribute("userRole", principal.getRole());
            session.setAttribute("authenticated", true);
            
            log.info("✅ 세션에 사용자 정보 저장 완료");
            
            String redirectUrl = determineRedirectUrl(principal.getRole());
            
            log.info("🔄 리다이렉트 URL: {}", redirectUrl);
            
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
            log.info("🚀 리다이렉트 전송 완료");
            
        } catch (Exception e) {
            log.error("❌ OAuth 로그인 성공 처리 중 오류 발생", e);
            response.sendRedirect("/auth.html?error=login_processing_failed");
        }
    }

    private String determineRedirectUrl(String role) {
        if ("ADMIN".equals(role)) {
            log.info("👑 관리자 역할 - 관리자 대시보드로 이동");
            return "/admin-dashboard.html";
        } else {
            log.info("👥 일반 사용자 - 대시보드로 이동");
            return "/dashboard.html";
        }
    }
}