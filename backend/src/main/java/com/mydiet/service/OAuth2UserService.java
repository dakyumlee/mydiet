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

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        log.info("OAuth2 로그인 - 제공자: {}, 속성: {}", registrationId, attributes);
        
        User user = saveOrUpdateUser(attributes, registrationId);
        
        return new OAuth2UserPrincipal(user, attributes, userNameAttributeName);
    }

    private User saveOrUpdateUser(Map<String, Object> attributes, String registrationId) {
        String email = extractEmail(attributes, registrationId);
        String nickname = extractNickname(attributes, registrationId);
        
        log.info("사용자 정보 처리 - 이메일: {}, 닉네임: {}", email, nickname);
        
        User user = userRepository.findByEmail(email)
            .orElse(User.builder()
                .email(email)
                .nickname(nickname)
                .role("USER")
                .weightGoal(60.0)
                .emotionMode("다정함")
                .createdAt(LocalDateTime.now())
                .build());
        
        // 기존 사용자 정보 업데이트
        if (user.getId() != null) {
            user.setNickname(nickname);
            user.setUpdatedAt(LocalDateTime.now());
        }
        
        User savedUser = userRepository.save(user);
        log.info("사용자 저장 완료 - ID: {}, 이메일: {}", savedUser.getId(), savedUser.getEmail());
        
        return savedUser;
    }

    private String extractEmail(Map<String, Object> attributes, String registrationId) {
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                return (String) kakaoAccount.get("email");
            }
        } else if ("google".equals(registrationId)) {
            return (String) attributes.get("email");
        }
        return null;
    }

    private String extractNickname(Map<String, Object> attributes, String registrationId) {
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    return (String) profile.get("nickname");
                }
            }
        } else if ("google".equals(registrationId)) {
            return (String) attributes.get("name");
        }
        return "사용자";
    }
}