package com.mydiet.controller;

import com.mydiet.service.OAuth2UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class RedirectController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/auth")
    public String auth() {
        return "forward:/auth.html";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) {
            log.warn("인증되지 않은 사용자가 대시보드 접근 시도");
            return "redirect:/auth";
        }
        
        log.debug("대시보드 접근 - 사용자: {}, 이메일: {}", principal.getNickname(), principal.getEmail());
        
        model.addAttribute("user", principal.getUser());
        model.addAttribute("nickname", principal.getNickname());
        model.addAttribute("email", principal.getEmail());
        model.addAttribute("userId", principal.getUserId());
        
        return "forward:/dashboard.html";
    }

    @GetMapping("/meal-management")
    public String mealManagement(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) return "redirect:/auth";
        model.addAttribute("user", principal.getUser());
        return "forward:/meal-management.html";
    }

    @GetMapping("/workout-management") 
    public String workoutManagement(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) return "redirect:/auth";
        model.addAttribute("user", principal.getUser());
        return "forward:/workout-management.html";
    }

    @GetMapping("/emotion-diary")
    public String emotionDiary(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) return "redirect:/auth";
        model.addAttribute("user", principal.getUser());
        return "forward:/emotion-diary.html";
    }

    @GetMapping("/analytics")
    public String analytics(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) return "redirect:/auth";
        model.addAttribute("user", principal.getUser());
        return "forward:/analytics.html";
    }

    @GetMapping("/profile-settings")
    public String profileSettings(@AuthenticationPrincipal OAuth2UserPrincipal principal, Model model) {
        if (principal == null) return "redirect:/auth";
        model.addAttribute("user", principal.getUser());
        return "forward:/profile-settings.html";
    }
}