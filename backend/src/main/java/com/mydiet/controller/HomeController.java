package com.mydiet.controller;

import com.mydiet.service.OAuth2UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        log.info("=== 홈 페이지 접근 ===");
        
        if (principal == null) {
            log.info("비인증 사용자 - index.html 표시");
            return "forward:/index.html";
        }
        
        log.info("인증된 사용자 - 이메일: {}, 역할: {}", principal.getEmail(), principal.getRole());
        
        if ("ADMIN".equals(principal.getRole())) {
            log.info("관리자 사용자 - 관리자 대시보드로 리다이렉트");
            return "redirect:/admin-dashboard.html";
        } else {
            log.info("일반 사용자 - 대시보드로 리다이렉트");
            return "redirect:/dashboard.html";
        }
    }

    @GetMapping("/auth")
    public String authPage() {
        log.info("Auth 페이지 요청");
        return "forward:/auth.html";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        log.info("=== Dashboard 리다이렉트 요청 ===");
        
        if (principal == null) {
            log.warn("비인증 사용자의 대시보드 접근 시도");
            return "redirect:/auth.html";
        }
        
        log.info("인증된 사용자 대시보드 접근: {}", principal.getEmail());
        return "forward:/dashboard.html";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboardRedirect(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        log.info("=== Admin Dashboard 리다이렉트 요청 ===");
        
        if (principal == null) {
            log.warn("비인증 사용자의 관리자 대시보드 접근 시도");
            return "redirect:/auth.html";
        }
        
        if (!"ADMIN".equals(principal.getRole())) {
            log.warn("권한 없는 사용자의 관리자 대시보드 접근: {}", principal.getEmail());
            return "redirect:/dashboard.html";
        }
        
        log.info("관리자 대시보드 접근: {}", principal.getEmail());
        return "forward:/admin-dashboard.html";
    }
}