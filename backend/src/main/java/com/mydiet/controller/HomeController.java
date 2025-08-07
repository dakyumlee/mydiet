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
        
        log.info("인증된 사용자 - 이메일: {}", principal.getEmail());
        
        return "redirect:/dashboard.html";
    }

    @GetMapping("/dashboard")
    public String dashboardRedirect(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        if (principal == null) {
            return "redirect:/auth.html";
        }
        return "forward:/dashboard.html";
    }
}