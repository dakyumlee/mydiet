package com.mydiet.service;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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
        log.info("OAuth2UserService.loadUser called");
        log.info("Client Registration: {}", userRequest.getClientRegistration().getRegistrationId());
        
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        if ("kakao".equals(registrationId)) {
            return processKakaoUser(oAuth2User);
        } else if ("google".equals(registrationId)) {
            return processGoogleUser(oAuth2User);
        }
        
        OAuth2Error oauth2Error = new OAuth2Error("invalid_provider", 
            "Unsupported provider: " + registrationId, null);
        throw new OAuth2AuthenticationException(oauth2Error);
    }

    private OAuth2User processKakaoUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 카카오 응답 구조
        // id는 최상위에 있음
        Long kakaoId = ((Number) attributes.get("id")).longValue();
        
        // kakao_account 안에 이메일과 프로필 정보
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        
        // 이메일 처리 - 동의하지 않았을 수도 있음
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        if (email == null) {
            email = kakaoId + "@kakao.user"; // 이메일이 없으면 임시 이메일 생성
        }
        
        // 프로필 정보
        String nickname = "카카오유저";
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                nickname = (String) profile.get("nickname");
                if (nickname == null) nickname = "카카오유저";
            }
        }
        
        log.info("Kakao user - ID: {}, Email: {}, Nickname: {}", kakaoId, email, nickname);

        // DB에서 사용자 찾기 또는 생성
        final String finalEmail = email;
        final String finalNickname = nickname;
        
        User user = userRepository.findByEmail(finalEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(finalEmail);
                    newUser.setNickname(finalNickname);
                    newUser.setSocialId(String.valueOf(kakaoId));
                    newUser.setSocialProvider("kakao");
                    newUser.setCreatedAt(LocalDateTime.now());
                    
                    log.info("Creating new user: {}", finalEmail);
                    return userRepository.save(newUser);
                });

        // 로그인 시간 업데이트
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }
    
    private OAuth2User processGoogleUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // Google 사용자 정보
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        
        log.info("Google user - ID: {}, Email: {}, Name: {}", googleId, email, name);
        
        // DB에서 사용자 찾기 또는 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setNickname(name != null ? name : "구글유저");
                    newUser.setSocialId(googleId);
                    newUser.setSocialProvider("google");
                    newUser.setCreatedAt(LocalDateTime.now());
                    
                    log.info("Creating new Google user: {}", email);
                    return userRepository.save(newUser);
                });
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }
}