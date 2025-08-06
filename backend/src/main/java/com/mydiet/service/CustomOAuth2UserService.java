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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("=== CustomOAuth2UserService.loadUser 시작 ===");
        
        OAuth2User oauth2User = super.loadUser(userRequest);
        log.info("OAuth2User loaded: {}", oauth2User.getName());
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("Registration ID: {}", registrationId);
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        User user = null;
        
        if ("kakao".equals(registrationId)) {
            Long kakaoId = (Long) attributes.get("id");
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            String email = (String) kakaoAccount.get("email");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = (String) profile.get("nickname");
            
            user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setNickname(nickname);
                        newUser.setSocialId(String.valueOf(kakaoId));
                        newUser.setSocialProvider("kakao");
                        newUser.setCreatedAt(LocalDateTime.now());
                        return userRepository.save(newUser);
                    });
        }
        
        return new OAuth2UserPrincipal(user, attributes);
    }
}