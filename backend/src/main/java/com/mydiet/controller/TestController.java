package com.mydiet.controller;

import com.mydiet.service.OAuth2UserPrincipal;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/auth-status")
    public Map<String, Object> getAuthStatus(@AuthenticationPrincipal OAuth2UserPrincipal principal, 
                                           HttpSession session) {
        
        Map<String, Object> status = new HashMap<>();
        
        log.info("=== 인증 상태 확인 API 호출 ===");
        
        if (principal != null) {
            status.put("authenticated", true);
            status.put("email", principal.getEmail());
            status.put("nickname", principal.getNickname());
            status.put("role", principal.getRole());
            status.put("userId", principal.getUserId());
            
            log.info("인증된 사용자: {}", principal.getEmail());
        } else {
            status.put("authenticated", false);
            log.info("비인증 사용자");
        }
        
        if (session != null) {
            status.put("sessionId", session.getId());
            status.put("sessionUserId", session.getAttribute("userId"));
            status.put("sessionEmail", session.getAttribute("userEmail"));
        }
        
        return status;
    }

    @GetMapping("/simple")
    public Map<String, String> simpleTest() {
        log.info("간단한 테스트 API 호출");
        return Map.of("status", "ok", "message", "API 정상 작동");
    }
}