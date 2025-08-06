package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import com.mydiet.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserRepository userRepository;

    /**
     * 실제 사용자 ID 찾기 (대시보드와 동일한 로직)
     */
    private Long getActualUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            log.info("세션에서 userId 찾음: {}", userId);
            return userId;
        }

        // 세션에 없으면 첫 번째 사용자 사용
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("사용자가 없습니다");
            return 1L; // 기본값
        }

        Long firstUserId = users.get(0).getId();
        log.info("첫 번째 사용자 ID 사용: {}", firstUserId);
        return firstUserId;
    }

    /**
     * 사용자 프로필 조회 - 대시보드와 동일한 사용자 ID 사용
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpSession session) {
        log.info("=== 사용자 프로필 조회 ===");
        
        try {
            Long userId = getActualUserId(session);
            log.info("사용할 userId: {}", userId);

            // 사용자 조회
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                log.warn("사용자를 찾을 수 없음: userId={}", userId);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "사용자를 찾을 수 없습니다",
                    "userId", userId
                ));
            }

            User user = userOpt.get();
            log.info("사용자 프로필 조회 성공: ID={}, 닉네임={}", user.getId(), user.getNickname());

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("nickname", user.getNickname() != null ? user.getNickname() : "사용자");
            profile.put("email", user.getEmail());
            profile.put("weightGoal", user.getWeightGoal());
            profile.put("emotionMode", user.getEmotionMode() != null ? user.getEmotionMode() : "다정함");
            profile.put("createdAt", user.getCreatedAt());
            profile.put("message", "프로필 조회 성공");

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            log.error("사용자 프로필 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "프로필 조회 중 오류가 발생했습니다: " + e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 프로필 업데이트
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody UpdateProfileRequest request, 
            HttpSession session) {
        
        log.info("=== 프로필 업데이트 ===");
        log.info("요청 데이터: {}", request);
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                userId = 1L; // 기본값
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            User user = userOpt.get();

            // 업데이트
            if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
                user.setNickname(request.getNickname().trim());
            }
            if (request.getWeightGoal() != null && request.getWeightGoal() > 0) {
                user.setWeightGoal(request.getWeightGoal());
            }
            if (request.getEmotionMode() != null && !request.getEmotionMode().trim().isEmpty()) {
                user.setEmotionMode(request.getEmotionMode().trim());
            }

            User updated = userRepository.save(user);
            log.info("프로필 업데이트 완료: 닉네임={}", updated.getNickname());

            return ResponseEntity.ok(Map.of(
                "message", "프로필이 성공적으로 업데이트되었습니다",
                "user", updated
            ));

        } catch (Exception e) {
            log.error("프로필 업데이트 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "업데이트 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }


}