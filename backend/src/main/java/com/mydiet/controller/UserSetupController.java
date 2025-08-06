package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/user-setup")
@RequiredArgsConstructor
@Slf4j
public class UserSetupController {

    private final UserRepository userRepository;

    /**
     * 현재 사용자 정보 완전히 업데이트
     */
    @PostMapping("/update-current")
    public ResponseEntity<Map<String, Object>> updateCurrentUser() {
        log.info("=== 현재 사용자 정보 업데이트 ===");

        try {
            // 첫 번째 사용자 가져오기
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자가 없습니다"));
            }

            User user = users.get(0);
            log.info("업데이트할 사용자: ID={}, 기존 닉네임={}", user.getId(), user.getNickname());

            // 정보 업데이트
            user.setNickname("MyDiet 사용자");
            user.setEmail("user@mydiet.com");
            user.setWeightGoal(65.0);
            user.setEmotionMode("다정함");
            
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDateTime.now());
            }

            User updated = userRepository.save(user);
            log.info("사용자 정보 업데이트 완료: {}", updated.getNickname());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "사용자 정보가 업데이트되었습니다",
                "user", updated
            ));

        } catch (Exception e) {
            log.error("사용자 정보 업데이트 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 완전한 사용자 정보로 새로 생성
     */
    @PostMapping("/create-complete")
    public ResponseEntity<Map<String, Object>> createCompleteUser() {
        log.info("=== 완전한 사용자 생성 ===");

        try {
            User user = new User();
            user.setNickname("MyDiet 메인 사용자");
            user.setEmail("main@mydiet.com");
            user.setWeightGoal(70.0);
            user.setEmotionMode("츤데레");
            user.setCreatedAt(LocalDateTime.now());

            User saved = userRepository.save(user);
            log.info("완전한 사용자 생성 완료: ID={}, 닉네임={}", saved.getId(), saved.getNickname());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "완전한 사용자가 생성되었습니다",
                "user", saved
            ));

        } catch (Exception e) {
            log.error("완전한 사용자 생성 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 모든 사용자 정보 표시
     */
    @GetMapping("/list-all")
    public ResponseEntity<Map<String, Object>> listAllUsers() {
        log.info("=== 모든 사용자 정보 ===");

        try {
            List<User> users = userRepository.findAll();
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalUsers", users.size());
            result.put("users", users);

            // 각 사용자 상세 정보 로그
            for (User user : users) {
                log.info("사용자 ID: {}, 닉네임: {}, 이메일: {}, 목표체중: {}, 감정모드: {}, 생성일: {}",
                        user.getId(), user.getNickname(), user.getEmail(), 
                        user.getWeightGoal(), user.getEmotionMode(), user.getCreatedAt());
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("사용자 목록 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 특정 사용자 ID로 강제 프로필 조회
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getProfileById(@PathVariable Long userId) {
        log.info("=== 특정 사용자 프로필 조회: userId={} ===", userId);

        try {
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "사용자를 찾을 수 없습니다",
                    "userId", userId
                ));
            }

            User user = userOpt.get();
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("nickname", user.getNickname());
            profile.put("email", user.getEmail());
            profile.put("weightGoal", user.getWeightGoal());
            profile.put("emotionMode", user.getEmotionMode());
            profile.put("createdAt", user.getCreatedAt());

            log.info("프로필 조회 성공: ID={}, 닉네임={}, 이메일={}", 
                    user.getId(), user.getNickname(), user.getEmail());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            log.error("특정 사용자 프로필 조회 실패: userId={}", userId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage(),
                "userId", userId
            ));
        }
    }
}