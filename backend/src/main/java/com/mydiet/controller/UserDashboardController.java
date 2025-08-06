package com.mydiet.controller;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserDashboardController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;

    /**
     * 현재 사용자 ID 가져오기 (개선된 버전)
     */
    private Long getCurrentUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        log.info("세션에서 userId 조회: {}", userId);
        
        if (userId != null) {
            // 사용자 존재 여부 확인
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                log.info("유효한 사용자 ID: {}", userId);
                return userId;
            } else {
                log.warn("세션의 userId {}에 해당하는 사용자 없음", userId);
            }
        }

        // 세션에 userId가 없거나 유효하지 않으면, 첫 번째 사용자 사용하고 세션에 저장
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.error("등록된 사용자가 없습니다");
            return null;
        }

        User firstUser = users.get(0);
        session.setAttribute("userId", firstUser.getId());
        log.info("첫 번째 사용자를 세션에 설정: ID={}, 닉네임={}", firstUser.getId(), firstUser.getNickname());
        
        return firstUser.getId();
    }

    /**
     * 현재 로그인 사용자 정보 확인
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        log.info("=== 현재 사용자 정보 요청 ===");
        
        try {
            Long userId = getCurrentUserId(session);
            if (userId == null) {
                return ResponseEntity.ok(Map.of(
                    "error", "사용자가 없습니다",
                    "suggestion", "먼저 사용자를 생성하세요"
                ));
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.ok(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("nickname", user.getNickname());
            result.put("email", user.getEmail());
            result.put("weightGoal", user.getWeightGoal());
            result.put("emotionMode", user.getEmotionMode());
            result.put("role", user.getRole());
            result.put("sessionId", session.getId());

            log.info("현재 사용자: ID={}, 닉네임={}", user.getId(), user.getNickname());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("현재 사용자 정보 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 사용자 프로필 조회 (경로 변경)
     */
    @GetMapping("/dashboard-profile")
    public ResponseEntity<Map<String, Object>> getDashboardProfile(HttpSession session) {
        log.info("=== 사용자 프로필 조회 ===");
        
        try {
            Long userId = getCurrentUserId(session);
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자 인증이 필요합니다"));
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("nickname", user.getNickname() != null ? user.getNickname() : "사용자");
            profile.put("email", user.getEmail());
            profile.put("weightGoal", user.getWeightGoal() != null ? user.getWeightGoal() : 65.0);
            profile.put("emotionMode", user.getEmotionMode() != null ? user.getEmotionMode() : "다정함");
            profile.put("createdAt", user.getCreatedAt());
            profile.put("role", user.getRole());

            log.info("프로필 조회 성공: 사용자 ID={}, 닉네임={}", user.getId(), user.getNickname());
            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            log.error("프로필 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 프로필 업데이트
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("=== 프로필 업데이트 ===");
        log.info("요청 데이터: {}", request);
        
        try {
            Long userId = getCurrentUserId(session);
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자 인증이 필요합니다"));
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            // 업데이트
            if (request.containsKey("nickname") && request.get("nickname") != null) {
                user.setNickname(request.get("nickname").toString());
            }
            if (request.containsKey("email") && request.get("email") != null) {
                user.setEmail(request.get("email").toString());
            }
            if (request.containsKey("weightGoal") && request.get("weightGoal") != null) {
                user.setWeightGoal(Double.valueOf(request.get("weightGoal").toString()));
            }
            if (request.containsKey("emotionMode") && request.get("emotionMode") != null) {
                user.setEmotionMode(request.get("emotionMode").toString());
            }

            User updated = userRepository.save(user);
            log.info("프로필 업데이트 완료: 사용자 ID={}, 닉네임={}", updated.getId(), updated.getNickname());

            return ResponseEntity.ok(Map.of(
                "message", "프로필이 성공적으로 업데이트되었습니다",
                "user", Map.of(
                    "id", updated.getId(),
                    "nickname", updated.getNickname(),
                    "email", updated.getEmail(),
                    "weightGoal", updated.getWeightGoal(),
                    "emotionMode", updated.getEmotionMode()
                )
            ));

        } catch (Exception e) {
            log.error("프로필 업데이트 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 대시보드 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            if (userId == null) {
                return ResponseEntity.ok(getEmptyStats());
            }

            final LocalDate today = LocalDate.now();
            
            log.info("=== 대시보드 통계 요청: userId={}, date={} ===", userId, today);

            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            log.info("데이터 조회 결과 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    todayMeals.size(), todayWorkouts.size(), todayEmotions.size());

            // 통계 계산
            int mealCount = todayMeals.size();
            int totalCaloriesBurned = todayWorkouts.stream()
                    .mapToInt(w -> w.getCaloriesBurned() != null ? w.getCaloriesBurned() : 0)
                    .sum();
            int totalCaloriesConsumed = todayMeals.stream()
                    .mapToInt(m -> m.getCaloriesEstimate() != null ? m.getCaloriesEstimate() : 0)
                    .sum();
            int totalWorkoutMinutes = todayWorkouts.stream()
                    .mapToInt(w -> w.getDuration() != null ? w.getDuration() : 0)
                    .sum();

            double goalAchievement = totalCaloriesConsumed > 0 ? 
                    Math.min(100.0, (double) totalCaloriesConsumed / 2000 * 100) : 0.0;

            Map<String, Object> stats = new HashMap<>();
            stats.put("mealCount", mealCount);
            stats.put("burnedCalories", totalCaloriesBurned);
            stats.put("consumedCalories", totalCaloriesConsumed);
            stats.put("workoutMinutes", totalWorkoutMinutes);
            stats.put("goalAchievement", Math.round(goalAchievement * 10.0) / 10.0);
            stats.put("userId", userId); // 디버깅용

            log.info("통계 결과: {}", stats);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("통계 조회 실패", e);
            return ResponseEntity.ok(getEmptyStats());
        }
    }

    /**
     * 오늘의 데이터 조회
     */
    @GetMapping("/today-data")
    public ResponseEntity<Map<String, Object>> getTodayData(HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            if (userId == null) {
                return ResponseEntity.ok(getEmptyTodayData());
            }

            final LocalDate today = LocalDate.now();
            log.info("=== 오늘 데이터 요청: userId={}, date={} ===", userId, today);

            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);

            log.info("오늘 데이터 조회 결과 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    meals.size(), workouts.size(), emotions.size());

            Map<String, Object> result = new HashMap<>();
            result.put("meals", meals);
            result.put("workouts", workouts);
            result.put("emotions", emotions);
            result.put("date", today.toString());
            result.put("userId", userId);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("오늘 데이터 조회 실패", e);
            return ResponseEntity.ok(getEmptyTodayData());
        }
    }

    private Map<String, Object> getEmptyStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("mealCount", 0);
        stats.put("burnedCalories", 0);
        stats.put("consumedCalories", 0);
        stats.put("workoutMinutes", 0);
        stats.put("goalAchievement", 0.0);
        return stats;
    }

    private Map<String, Object> getEmptyTodayData() {
        Map<String, Object> data = new HashMap<>();
        data.put("meals", new ArrayList<>());
        data.put("workouts", new ArrayList<>());
        data.put("emotions", new ArrayList<>());
        data.put("date", LocalDate.now().toString());
        return data;
    }
}