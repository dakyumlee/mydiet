package com.mydiet.controller;

import com.mydiet.service.OAuth2UserPrincipal;
import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        if (principal == null) {
            log.warn("No authenticated user found");
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        User user = userRepository.findById(principal.getUserId())
                .orElse(null);
                
        if (user == null) {
            log.error("User not found for id: {}", principal.getUserId());
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("isAuthenticated", true);
        
        log.info("User info retrieved for: {}", user.getEmail());
        return ResponseEntity.ok(userInfo);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal OAuth2UserPrincipal principal,
                                          @RequestBody Map<String, Object> updates) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            User user = userRepository.findById(principal.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
             
            if (updates.containsKey("nickname")) {
                user.setNickname((String) updates.get("nickname"));
            }
            if (updates.containsKey("weightGoal")) {
                user.setWeightGoal(Double.valueOf(updates.get("weightGoal").toString()));
            }
            if (updates.containsKey("emotionMode")) {
                user.setEmotionMode((String) updates.get("emotionMode"));
            }
            
            User savedUser = userRepository.save(user);
            log.info("Profile updated for user: {}", savedUser.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "프로필이 업데이트되었습니다"
            ));
            
        } catch (Exception e) {
            log.error("Error updating profile: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}