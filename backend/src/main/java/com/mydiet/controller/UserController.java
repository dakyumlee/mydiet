package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import com.mydiet.service.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2UserPrincipal principal,
                                           HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null && principal != null) {
            userId = principal.getUserId();
        }
        
        if (userId == null) {
            Map<String, Object> defaultUser = new HashMap<>();
            defaultUser.put("id", 1L);
            defaultUser.put("email", "guest@mydiet.com");
            defaultUser.put("nickname", "Guest");
            defaultUser.put("isAuthenticated", false);
            return ResponseEntity.ok(defaultUser);
        }
        
        User user = userRepository.findById(userId).orElse(null);
        
        if (user == null) {
            Map<String, Object> defaultUser = new HashMap<>();
            defaultUser.put("id", userId);
            defaultUser.put("email", "unknown@mydiet.com");
            defaultUser.put("nickname", "Unknown");
            defaultUser.put("isAuthenticated", false);
            return ResponseEntity.ok(defaultUser);
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("isAuthenticated", true);
        
        return ResponseEntity.ok(userInfo);
    }
}