package com.mydiet.controller;

import com.mydiet.service.OAuth2UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class RedirectController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) {
            log.warn("인증되지 않은 대시보드 접근 시도");
            return "redirect:/auth.html";
        }

        try {
            log.debug("대시보드 접근 - 사용자: {}, 이메일: {}", principal.getNickname(), principal.getEmail());
            
            // 모델에 사용자 정보 추가
            model.addAttribute("nickname", principal.getNickname());
            model.addAttribute("email", principal.getEmail());
            model.addAttribute("userId", principal.getUserId());
            model.addAttribute("role", principal.getRole());
            model.addAttribute("weightGoal", principal.getWeightGoal());
            model.addAttribute("emotionMode", principal.getEmotionMode());
            
            // 관리자인 경우 관리자 대시보드로 리다이렉트
            if ("ADMIN".equals(principal.getRole())) {
                log.info("관리자 사용자 - 관리자 대시보드로 리다이렉트: {}", principal.getEmail());
                return "redirect:/admin-dashboard.html";
            }
            
            // 일반 사용자는 대시보드로
            return "redirect:/dashboard.html";
            
        } catch (Exception e) {
            log.error("대시보드 접근 중 오류 발생", e);
            return "redirect:/auth.html?error=dashboard_error";
        }
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) {
            log.warn("인증되지 않은 관리자 대시보드 접근 시도");
            return "redirect:/admin-login.html";
        }

        if (!"ADMIN".equals(principal.getRole())) {
            log.warn("권한 없는 관리자 대시보드 접근: {}", principal.getEmail());
            return "redirect:/dashboard.html";
        }

        try {
            log.info("관리자 대시보드 접근: {}", principal.getEmail());
            
            model.addAttribute("adminNickname", principal.getNickname());
            model.addAttribute("adminEmail", principal.getEmail());
            model.addAttribute("adminId", principal.getUserId());
            
            return "redirect:/admin-dashboard.html";
            
        } catch (Exception e) {
            log.error("관리자 대시보드 접근 중 오류 발생", e);
            return "redirect:/admin-login.html?error=admin_error";
        }
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        if (principal == null) {
            return "redirect:/auth.html";
        }
        
        // 관리자는 관리자 대시보드로, 일반 사용자는 대시보드로
        if ("ADMIN".equals(principal.getRole())) {
            return "redirect:/admin-dashboard.html";
        } else {
            return "redirect:/dashboard.html";
        }
    }
}