package com.mydiet.config;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        try {
            String email = null;
            String nickname = null;
            String provider = null;

            if (oauth2User.getAttributes().containsKey("email")) {
                email = oauth2User.getAttribute("email");
                nickname = oauth2User.getAttribute("name");
                provider = "Google";
            }
            else if (oauth2User.getAttributes().containsKey("kakao_account")) {
                Object kakaoAccount = oauth2User.getAttribute("kakao_account");
                if (kakaoAccount instanceof java.util.Map) {
                    java.util.Map<String, Object> account = (java.util.Map<String, Object>) kakaoAccount;
                    email = (String) account.get("email");
                    
                    Object profile = account.get("profile");
                    if (profile instanceof java.util.Map) {
                        java.util.Map<String, Object> profileMap = (java.util.Map<String, Object>) profile;
                        nickname = (String) profileMap.get("nickname");
                    }
                }
                provider = "Kakao";
            }

            log.info("OAuth 로그인 성공 - Provider: {}, Email: {}, Nickname: {}", provider, email, nickname);

            if (email != null) {
                Optional<User> existingUser = userRepository.findByEmail(email);
                
                if (existingUser.isEmpty()) {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setNickname(nickname != null ? nickname : "익명사용자");
                    newUser.setWeightGoal(70.0);
                    newUser.setEmotionMode("다정함");
                    newUser.setCreatedAt(LocalDateTime.now());
                    
                    userRepository.save(newUser);
                    log.info("새 사용자 생성 완료 - {}", email);
                    
                    response.sendRedirect("/welcome?new=true");
                } else {
                    log.info("기존 사용자 로그인 - {}", email);
                    response.sendRedirect("/dashboard");
                }
            } else {
                log.error("OAuth 로그인에서 이메일을 가져올 수 없음");
                response.sendRedirect("/login?error=email_missing");
            }
            
        } catch (Exception e) {
            log.error("OAuth 로그인 처리 중 오류 발생", e);
            response.sendRedirect("/login?error=oauth_error");
        }
    }
}