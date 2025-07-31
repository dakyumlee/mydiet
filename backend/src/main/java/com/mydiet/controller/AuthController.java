package com.mydiet.controller;

import com.mydiet.dto.SignupRequest;
import com.mydiet.dto.LoginRequest;
import com.mydiet.dto.UserResponse;
import com.mydiet.entity.User;
import com.mydiet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "이미 사용 중인 이메일입니다."));
            }

            User user = userService.createUser(request);
            
            UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .weightGoal(user.getWeightGoal())
                .emotionMode(user.getEmotionMode())
                .createdAt(user.getCreatedAt())
                .build();

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "회원가입에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());
            
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "이메일 또는 비밀번호가 잘못되었습니다."));
            }

            userService.updateLastLogin(user.getId());

            UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .weightGoal(user.getWeightGoal())
                .emotionMode(user.getEmotionMode())
                .createdAt(user.getCreatedAt())
                .build();

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "로그인에 실패했습니다."));
        }
    }
}