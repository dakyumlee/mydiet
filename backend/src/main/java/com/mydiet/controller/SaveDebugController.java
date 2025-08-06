package com.mydiet.controller;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import com.mydiet.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class SaveDebugController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;

    /**
     * 모든 데이터 조회 (디버깅용)
     */
    @GetMapping("/all-data")
    public ResponseEntity<Map<String, Object>> getAllData() {
        log.info("=== 전체 데이터 조회 시작 ===");
        
        try {
            List<User> users = userRepository.findAll();
            List<MealLog> meals = mealLogRepository.findAll();
            List<WorkoutLog> workouts = workoutLogRepository.findAll();
            List<EmotionLog> emotions = emotionLogRepository.findAll();

            log.info("데이터 개수 - Users: {}, Meals: {}, Workouts: {}, Emotions: {}", 
                    users.size(), meals.size(), workouts.size(), emotions.size());

            Map<String, Object> data = new HashMap<>();
            data.put("users", users);
            data.put("meals", meals);
            data.put("workouts", workouts);
            data.put("emotions", emotions);
            data.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("전체 데이터 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 직접 식단 저장 테스트
     */
    @PostMapping("/test-meal")
    public ResponseEntity<Map<String, Object>> testMealSave(@RequestBody Map<String, Object> request) {
        log.info("=== 식단 저장 테스트 ===");
        log.info("요청 데이터: {}", request);

        try {
            // 사용자 조회
            Long userId = Long.valueOf(request.get("userId").toString());
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                log.warn("사용자를 찾을 수 없음: userId={}", userId);
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            User user = userOpt.get();
            log.info("사용자 찾음: {}", user.getNickname());

            // 식단 생성
            MealLog meal = new MealLog();
            meal.setUser(user);
            meal.setDescription(request.get("description").toString());
            meal.setCaloriesEstimate(Integer.valueOf(request.get("caloriesEstimate").toString()));
            meal.setDate(LocalDate.now());

            log.info("저장할 식단: {} ({}kcal)", meal.getDescription(), meal.getCaloriesEstimate());

            // 저장
            MealLog saved = mealLogRepository.save(meal);
            log.info("식단 저장 완료: ID={}", saved.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("savedMeal", saved);
            result.put("message", "식단이 성공적으로 저장되었습니다");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("식단 저장 테스트 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage(),
                "stackTrace", Arrays.toString(e.getStackTrace())
            ));
        }
    }

    /**
     * 직접 운동 저장 테스트
     */
    @PostMapping("/test-workout")
    public ResponseEntity<Map<String, Object>> testWorkoutSave(@RequestBody Map<String, Object> request) {
        log.info("=== 운동 저장 테스트 ===");
        log.info("요청 데이터: {}", request);

        try {
            // 사용자 조회
            Long userId = Long.valueOf(request.get("userId").toString());
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            User user = userOpt.get();

            // 운동 생성
            WorkoutLog workout = new WorkoutLog();
            workout.setUser(user);
            workout.setType(request.get("type").toString());
            workout.setDuration(Integer.valueOf(request.get("duration").toString()));
            workout.setCaloriesBurned(Integer.valueOf(request.get("caloriesBurned").toString()));
            workout.setDate(LocalDate.now());

            log.info("저장할 운동: {} {}분 ({}kcal)", workout.getType(), workout.getDuration(), workout.getCaloriesBurned());

            // 저장
            WorkoutLog saved = workoutLogRepository.save(workout);
            log.info("운동 저장 완료: ID={}", saved.getId());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "savedWorkout", saved,
                "message", "운동이 성공적으로 저장되었습니다"
            ));

        } catch (Exception e) {
            log.error("운동 저장 테스트 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 데이터베이스 연결 테스트
     */
    @GetMapping("/db-test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        log.info("=== 데이터베이스 연결 테스트 ===");

        Map<String, Object> result = new HashMap<>();
        
        try {
            // 각 테이블 개수 확인
            long userCount = userRepository.count();
            long mealCount = mealLogRepository.count();
            long workoutCount = workoutLogRepository.count();
            long emotionCount = emotionLogRepository.count();

            result.put("dbConnected", true);
            result.put("userCount", userCount);
            result.put("mealCount", mealCount);
            result.put("workoutCount", workoutCount);
            result.put("emotionCount", emotionCount);
            result.put("timestamp", LocalDateTime.now());

            log.info("DB 연결 성공 - Users: {}, Meals: {}, Workouts: {}, Emotions: {}", 
                    userCount, mealCount, workoutCount, emotionCount);

        } catch (Exception e) {
            log.error("데이터베이스 연결 테스트 실패", e);
            result.put("dbConnected", false);
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 오늘 데이터만 조회
     */
    @GetMapping("/today-only")
    public ResponseEntity<Map<String, Object>> getTodayOnly() {
        log.info("=== 오늘 데이터만 조회 ===");
        
        LocalDate today = LocalDate.now();
        log.info("조회 날짜: {}", today);

        try {
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(1L, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(1L, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(1L, today);

            Map<String, Object> result = new HashMap<>();
            result.put("date", today.toString());
            result.put("meals", todayMeals);
            result.put("workouts", todayWorkouts);
            result.put("emotions", todayEmotions);
            result.put("mealCount", todayMeals.size());
            result.put("workoutCount", todayWorkouts.size());
            result.put("emotionCount", todayEmotions.size());

            log.info("오늘 데이터 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    todayMeals.size(), todayWorkouts.size(), todayEmotions.size());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("오늘 데이터 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}