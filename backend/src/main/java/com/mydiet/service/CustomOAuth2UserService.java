package com.mydiet.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            
            System.out.println("=== OAuth2 Debug Info ===");
            System.out.println("Registration ID: " + registrationId);
            System.out.println("Attributes: " + attributes);
            
            if ("kakao".equals(registrationId)) {
                try {
                    Object kakaoAccountObj = attributes.get("kakao_account");
                    String email = null;
                    String nickname = "Unknown";
                    
                    if (kakaoAccountObj instanceof Map) {
                        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
                        
                        email = (String) kakaoAccount.get("email");
                        
                        Object profileObj = kakaoAccount.get("profile");
                        if (profileObj instanceof Map) {
                            Map<String, Object> profile = (Map<String, Object>) profileObj;
                            nickname = (String) profile.get("nickname");
                        }
                    }
                    
                    String id = String.valueOf(attributes.get("id"));
                    attributes.put("email", email != null ? email : id + "@kakao.local"); // 이메일이 없으면 임시 이메일
                    attributes.put("name", nickname != null ? nickname : "카카오유저");
                    attributes.put("id", id);
                    
                    System.out.println("카카오 처리 완료 - ID: " + id + ", 닉네임: " + nickname + ", 이메일: " + attributes.get("email"));
                    
                } catch (Exception e) {
                    System.err.println("카카오 정보 처리 중 에러: " + e.getMessage());
                    String id = String.valueOf(attributes.get("id"));
                    attributes.put("email", id + "@kakao.local");
                    attributes.put("name", "카카오유저");
                    attributes.put("id", id);
                }
                
                return new DefaultOAuth2User(
                    oAuth2User.getAuthorities(),
                    attributes,
                    "id"
                );
            }
             
            return oAuth2User;
            
        } catch (Exception e) {
            System.err.println("OAuth2 처리 중 에러: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("OAuth2 처리 실패: " + e.getMessage());
        }
    }
}