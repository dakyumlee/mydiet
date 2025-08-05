package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setWeightGoal(user.getWeightGoal());
        response.setEmotionMode(user.getEmotionMode());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<String> updateUserProfile(@RequestBody UpdateProfileRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
        }
        
        if (request.getWeightGoal() != null && request.getWeightGoal() > 0) {
            user.setWeightGoal(request.getWeightGoal());
        }
        
        if (request.getEmotionMode() != null && !request.getEmotionMode().trim().isEmpty()) {
            user.setEmotionMode(request.getEmotionMode());
        }
        
        userRepository.save(user);
        return ResponseEntity.ok("프로필이 업데이트되었습니다!");
    }

    private User getCurrentUser(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String oauthId = String.valueOf(oAuth2User.getAttributes().get("id"));
        String userIdentifier = email != null ? email : oauthId;
        
        return userRepository.findByEmail(userIdentifier)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    public static class UserProfileResponse {
        private Long id;
        private String nickname;
        private String email;
        private Double weightGoal;
        private String emotionMode;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Double getWeightGoal() { return weightGoal; }
        public void setWeightGoal(Double weightGoal) { this.weightGoal = weightGoal; }
        public String getEmotionMode() { return emotionMode; }
        public void setEmotionMode(String emotionMode) { this.emotionMode = emotionMode; }
    }

    public static class UpdateProfileRequest {
        private String nickname;
        private Double weightGoal;
        private String emotionMode;
        
        // Getters and Setters
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public Double getWeightGoal() { return weightGoal; }
        public void setWeightGoal(Double weightGoal) { this.weightGoal = weightGoal; }
        public String getEmotionMode() { return emotionMode; }
        public void setEmotionMode(String emotionMode) { this.emotionMode = emotionMode; }
    }
}