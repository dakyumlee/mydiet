package com.mydiet.controller;

import com.mydiet.dto.LoginRequest;
import com.mydiet.dto.SignupRequest;
import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "이미 가입된 이메일입니다."));
        }

        if (request.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "비밀번호는 8자 이상이어야 합니다."));
        }

        User user = new User();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setWeightGoal(request.getWeightGoal());
        user.setEmotionMode(request.getEmotionMode());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", true, "message", "회원가입 성공", "user", user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "존재하지 않는 이메일입니다."));
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "비밀번호가 올바르지 않습니다."));
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "로그인 성공",
            "user", Map.of(
                "id", user.getId(),
                "nickname", user.getNickname(),
                "email", user.getEmail(),
                "weightGoal", user.getWeightGoal(),
                "emotionMode", user.getEmotionMode(),
                "createdAt", user.getCreatedAt()
            )
        ));
    }

    @GetMapping("/users/count")
    public ResponseEntity<?> getUserCount() {
        return ResponseEntity.ok(Map.of("count", userRepository.count()));
    }
}
