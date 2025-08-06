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
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("OAuth2 사용자 처리 실패", ex);
            throw new OAuth2AuthenticationException("OAuth2 인증 처리 중 오류가 발생했습니다");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        log.info("OAuth2 로그인 시도: provider={}, attributes={}", registrationId, attributes);

        User user = null;
        
        if ("kakao".equals(registrationId)) {
            user = processKakaoUser(attributes);
        } else if ("google".equals(registrationId)) {
            user = processGoogleUser(attributes);
        }

        if (user != null) {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("OAuth2 로그인 성공: 사용자 ID={}, 닉네임={}", user.getId(), user.getNickname());
        }

        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }

    private User processKakaoUser(Map<String, Object> attributes) {
        Long kakaoId = Long.valueOf(attributes.get("id").toString());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        
        String nickname = (String) profile.get("nickname");
        String email = (String) kakaoAccount.get("email");

        // 기존 사용자 찾기
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // 카카오 정보 업데이트
            user.setSocialId(String.valueOf(kakaoId));
            user.setSocialProvider("kakao");
            user.setProvider("kakao");
            user.setProviderId(String.valueOf(kakaoId));
            return user;
        } else {
            // 새 사용자 생성
            User newUser = new User();
            newUser.setNickname(nickname);
            newUser.setEmail(email);
            newUser.setSocialId(String.valueOf(kakaoId));
            newUser.setSocialProvider("kakao");
            newUser.setProvider("kakao");
            newUser.setProviderId(String.valueOf(kakaoId));
            newUser.setWeightGoal(65.0);
            newUser.setEmotionMode("다정함");
            newUser.setCreatedAt(LocalDateTime.now());
            return userRepository.save(newUser);
        }
    }

    private User processGoogleUser(Map<String, Object> attributes) {
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 기존 사용자 찾기
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // 구글 정보 업데이트
            user.setSocialId(googleId);
            user.setSocialProvider("google");
            user.setProvider("google");
            user.setProviderId(googleId);
            return user;
        } else {
            // 새 사용자 생성
            User newUser = new User();
            newUser.setNickname(name);
            newUser.setEmail(email);
            newUser.setSocialId(googleId);
            newUser.setSocialProvider("google");
            newUser.setProvider("google");
            newUser.setProviderId(googleId);
            newUser.setWeightGoal(65.0);
            newUser.setEmotionMode("다정함");
            newUser.setCreatedAt(LocalDateTime.now());
            return userRepository.save(newUser);
        }
    }
}