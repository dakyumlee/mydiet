package com.mydiet.config;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("=== OAuth2LoginSuccessHandler 실행 ===");
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
 
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String oauthId = String.valueOf(attributes.get("id"));
         
        String provider = determineProvider(request);

        System.out.println("Provider: " + provider);
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("OAuth ID: " + oauthId);
 
        String userIdentifier = email != null ? email : oauthId + "@" + provider + ".local";

        try { 
            User user = userRepository.findByEmail(userIdentifier)
                    .orElseGet(() -> createNewUser(userIdentifier, name, oauthId, provider));
 
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);

            System.out.println("사용자 저장 완료: " + user.getNickname());
            System.out.println("Dashboard로 강제 리다이렉트 실행!");
 
            response.sendRedirect("/dashboard.html");
            
        } catch (Exception e) {
            System.err.println("사용자 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("/welcome");
        }
    }

    private String determineProvider(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("google")) return "google";
        if (uri.contains("kakao")) return "kakao";
        return "unknown";
    }

    private User createNewUser(String email, String name, String oauthId, String provider) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(name != null ? name : provider + "유저");
        user.setWeightGoal(65.0); // 기본 목표 체중
        user.setEmotionMode("다정함"); // 기본 감정 모드
        user.setCreatedAt(LocalDateTime.now());
        
        System.out.println("새 사용자 생성: " + user.getNickname());
        return user;
    }
}