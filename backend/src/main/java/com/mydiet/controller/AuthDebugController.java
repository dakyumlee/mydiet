package com.mydiet.controller;

import com.mydiet.service.OAuth2UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthDebugController {

    @GetMapping("/check")
    public Map<String, Object> checkAuth(@AuthenticationPrincipal OAuth2UserPrincipal principal,
                                       HttpServletRequest request,
                                       HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        log.info("=== 인증 상태 디버깅 ===");
         
        if (principal != null) {
            result.put("principal_exists", true);
            result.put("principal_email", principal.getEmail());
            result.put("principal_nickname", principal.getNickname());
            result.put("principal_role", principal.getRole());
            log.info("✅ Principal 존재: {}", principal.getEmail());
        } else {
            result.put("principal_exists", false);
            log.info("❌ Principal 없음");
        }
         
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            result.put("security_context_authenticated", true);
            result.put("security_context_name", auth.getName());
            result.put("security_context_authorities", auth.getAuthorities().toString());
            log.info("✅ SecurityContext 인증됨: {}", auth.getName());
        } else {
            result.put("security_context_authenticated", false);
            log.info("❌ SecurityContext 인증 안됨");
        }
         
        if (session != null) {
            result.put("session_exists", true);
            result.put("session_id", session.getId());
            result.put("session_user_id", session.getAttribute("userId"));
            result.put("session_email", session.getAttribute("userEmail"));
            result.put("session_authenticated", session.getAttribute("authenticated"));
            log.info("✅ 세션 존재: {}", session.getId());
        } else {
            result.put("session_exists", false);
            log.info("❌ 세션 없음");
        }
         
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    result.put("jsessionid_cookie", cookie.getValue());
                    log.info("✅ JSESSIONID 쿠키: {}", cookie.getValue());
                }
            }
        }
        
        return result;
    }
}