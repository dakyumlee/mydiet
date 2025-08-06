package com.mydiet.service;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.debug("OAuth2 로그인 제공자: {}", registrationId);
        log.debug("OAuth2 사용자 정보: {}", oauth2User.getAttributes());
        
        String email = null;
        String nickname = null;
        String providerId = null;
        
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");
            if (kakaoAccount != null) {
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                email = (String) kakaoAccount.get("email");
                if (profile != null) {
                    nickname = (String) profile.get("nickname");
                }
            }
            providerId = oauth2User.getName();
            
        } else if ("google".equals(registrationId)) {
            email = (String) oauth2User.getAttributes().get("email");
            nickname = (String) oauth2User.getAttributes().get("name");
            providerId = oauth2User.getName(); // 구글 ID
        }
        
        log.debug("추출된 정보 - 이메일: {}, 닉네임: {}, 제공자ID: {}", email, nickname, providerId);
        
        User user = processOAuthPostLogin(email, nickname, registrationId, providerId);
        
        return new OAuth2UserPrincipal(oauth2User, user);
    }
    
    private User processOAuthPostLogin(String email, String nickname, String provider, String providerId) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.debug("기존 사용자 로그인: {}", email);
             
            if (nickname != null && !nickname.equals(user.getNickname())) {
                user.setNickname(nickname);
                user = userRepository.save(user);
                log.debug("사용자 닉네임 업데이트: {}", nickname);
            }
            
            return user;
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNickname(nickname != null ? nickname : "사용자");
            newUser.setEmotionMode("다정함");
            newUser.setWeightGoal(60.0);
            newUser.setCreatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(newUser);
            log.debug("새 사용자 생성: {}", email);
            
            return savedUser;
        }
    }
}