package com.mydiet.controller;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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
     * 실제 사용자 ID 찾기 (첫 번째 사용자)
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
     * 사용자 대시보드 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpSession session) {
        try {
            Long userId = getActualUserId(session);
            final LocalDate today = LocalDate.now();
            
            log.info("=== 대시보드 통계 요청 ===");
            log.info("userId: {}, date: {}", userId, today);

            // 오늘의 데이터 조회
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            log.info("조회 결과 - userId: {}, 식단: {}개, 운동: {}개, 감정: {}개", 
                    userId, todayMeals.size(), todayWorkouts.size(), todayEmotions.size());

            // 통계 계산
            int mealCount = todayMeals.size();
            
            int totalCaloriesBurned = todayWorkouts.stream()
                    .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                    .sum();
            
            int totalCaloriesConsumed = todayMeals.stream()
                    .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                    .sum();

            // 운동 시간 합계 (분)
            int totalWorkoutMinutes = todayWorkouts.stream()
                    .mapToInt(workout -> workout.getDuration() != null ? workout.getDuration() : 0)
                    .sum();

            // 목표 달성도 계산 (칼로리 기준)
            double goalAchievement = 0.0;
            if (totalCaloriesConsumed > 0) {
                int dailyCalorieGoal = 2000;
                goalAchievement = Math.min(100.0, (double) totalCaloriesConsumed / dailyCalorieGoal * 100);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("mealCount", mealCount);
            stats.put("burnedCalories", totalCaloriesBurned);
            stats.put("consumedCalories", totalCaloriesConsumed);
            stats.put("workoutMinutes", totalWorkoutMinutes);
            stats.put("goalAchievement", Math.round(goalAchievement * 10.0) / 10.0);
            stats.put("userId", userId); // 디버깅용

            log.info("=== 최종 통계 결과 ===");
            log.info("식단 수: {}, 소모 칼로리: {}, 섭취 칼로리: {}, 운동 시간: {}분, 목표 달성도: {}%", 
                    mealCount, totalCaloriesBurned, totalCaloriesConsumed, totalWorkoutMinutes, goalAchievement);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("대시보드 통계 조회 중 오류 발생", e);
            
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("mealCount", 0);
            defaultStats.put("burnedCalories", 0);
            defaultStats.put("consumedCalories", 0);
            defaultStats.put("workoutMinutes", 0);
            defaultStats.put("goalAchievement", 0.0);
            defaultStats.put("error", e.getMessage());
            
            return ResponseEntity.ok(defaultStats);
        }
    }

    /**
     * 오늘의 모든 기록 조회
     */
    @GetMapping("/today-data")
    public ResponseEntity<Map<String, Object>> getTodayData(HttpSession session) {
        try {
            Long userId = getActualUserId(session);
            final LocalDate today = LocalDate.now();

            log.info("=== 오늘 데이터 요청 ===");
            log.info("userId: {}, date: {}", userId, today);

            // 오늘의 모든 데이터 조회
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);

            log.info("오늘 데이터 조회 결과 - userId: {}, 식단: {}개, 운동: {}개, 감정: {}개", 
                    userId, meals.size(), workouts.size(), emotions.size());

            // 데이터 상세 로그
            meals.forEach(meal -> 
                log.debug("식단: {} ({}kcal) - 사용자ID: {}", meal.getDescription(), meal.getCaloriesEstimate(), meal.getUser().getId()));
            workouts.forEach(workout -> 
                log.debug("운동: {} {}분 ({}kcal) - 사용자ID: {}", workout.getType(), workout.getDuration(), workout.getCaloriesBurned(), workout.getUser().getId()));
            emotions.forEach(emotion -> 
                log.debug("감정: {} - {} - 사용자ID: {}", emotion.getMood(), emotion.getNote(), emotion.getUser().getId()));

            Map<String, Object> result = new HashMap<>();
            result.put("meals", meals);
            result.put("workouts", workouts);
            result.put("emotions", emotions);
            result.put("date", today.toString());
            result.put("userId", userId); // 디버깅용

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("오늘 데이터 조회 중 오류 발생", e);
            
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("meals", new ArrayList<>());
            emptyResult.put("workouts", new ArrayList<>());
            emptyResult.put("emotions", new ArrayList<>());
            emptyResult.put("date", LocalDate.now().toString());
            emptyResult.put("error", e.getMessage());
            
            return ResponseEntity.ok(emptyResult);
        }
    }

    /**
     * 모든 사용자의 오늘 데이터 조회 (디버깅용)
     */
    @GetMapping("/today-all-users")
    public ResponseEntity<Map<String, Object>> getTodayAllUsers() {
        try {
            final LocalDate today = LocalDate.now();
            log.info("=== 모든 사용자 오늘 데이터 ===");

            List<User> allUsers = userRepository.findAll();
            Map<String, Object> result = new HashMap<>();

            for (User user : allUsers) {
                List<MealLog> meals = mealLogRepository.findByUserIdAndDate(user.getId(), today);
                List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(user.getId(), today);
                List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(user.getId(), today);

                if (meals.size() > 0 || workouts.size() > 0 || emotions.size() > 0) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("user", user);
                    userData.put("meals", meals);
                    userData.put("workouts", workouts);
                    userData.put("emotions", emotions);
                    
                    result.put("user_" + user.getId(), userData);
                    log.info("사용자 {} (ID: {}): 식단 {}개, 운동 {}개, 감정 {}개", 
                            user.getNickname(), user.getId(), meals.size(), workouts.size(), emotions.size());
                }
            }

            result.put("date", today.toString());
            result.put("totalUsers", allUsers.size());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("전체 사용자 데이터 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}