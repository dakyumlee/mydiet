package com.mydiet.service;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        String email = extractEmail(registrationId, attributes);
        String nickname = extractNickname(registrationId, attributes);
        
        User user = userRepository.findByEmail(email)
            .orElseGet(() -> createUser(email, nickname));
        
        return new OAuth2UserPrincipal(user, attributes);
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("email");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    private String extractNickname(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("name");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return (String) properties.get("nickname");
        }
        return "사용자";
    }

    private User createUser(String email, String nickname) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setWeightGoal(70.0);
        user.setEmotionMode("다정함");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}