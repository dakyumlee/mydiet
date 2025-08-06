package com.mydiet.controller;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimpleDebugController {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    
    // 전체 데이터 조회
    @GetMapping("/all-data")
    public ResponseEntity<?> getAllData() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("users", userRepository.findAll());
            data.put("meals", mealLogRepository.findAll());
            data.put("workouts", workoutLogRepository.findAll());
            data.put("emotions", emotionLogRepository.findAll());
            
            log.info("All data requested - Users: {}, Meals: {}, Workouts: {}, Emotions: {}", 
                userRepository.count(), mealLogRepository.count(), 
                workoutLogRepository.count(), emotionLogRepository.count());
                
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error fetching all data: ", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
    
    // 테스트 데이터 생성
    @PostMapping("/create-test-data")
    public ResponseEntity<?> createTestData() {
        try {
            log.info("Creating test data...");
            
            // 테스트 유저 생성
            User user = userRepository.findById(1L).orElse(null);
            if (user == null) {
                user = new User();
                user.setId(1L);
                user.setNickname("테스트사용자");
                user.setEmail("test@mydiet.com");
                user.setWeightGoal(65.0);
                user.setEmotionMode("보통");
                user.setCreatedAt(LocalDateTime.now());
                user = userRepository.save(user);
                log.info("Created test user: {}", user.getId());
            }
            
            // 테스트 식단 생성
            MealLog meal = new MealLog();
            meal.setUser(user);
            meal.setDescription("테스트 점심");
            meal.setCaloriesEstimate(500);
            meal.setDate(LocalDate.now());
            meal = mealLogRepository.save(meal);
            log.info("Created test meal: {}", meal.getId());
            
            // 테스트 감정 생성
            EmotionLog emotion = new EmotionLog();
            emotion.setUser(user);
            emotion.setMood("행복");
            emotion.setNote("기분이 좋아요");
            emotion.setDate(LocalDate.now());
            emotion = emotionLogRepository.save(emotion);
            log.info("Created test emotion: {}", emotion.getId());
            
            // 테스트 운동 생성
            WorkoutLog workout = new WorkoutLog();
            workout.setUser(user);
            workout.setType("걷기");
            workout.setDuration(30);
            workout.setCaloriesBurned(150);
            workout.setDate(LocalDate.now());
            workout = workoutLogRepository.save(workout);
            log.info("Created test workout: {}", workout.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "테스트 데이터 생성됨",
                "userId", user.getId(),
                "mealId", meal.getId(),
                "emotionId", emotion.getId(),
                "workoutId", workout.getId()
            ));
            
        } catch (Exception e) {
            log.error("Error creating test data: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false, 
                "error", e.getMessage()
            ));
        }
    }
    
    // 특정 사용자 데이터 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserData(@PathVariable Long userId) {
        try {
            LocalDate today = LocalDate.now();
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", userRepository.findById(userId));
            data.put("todayMeals", mealLogRepository.findByUserIdAndDate(userId, today));
            data.put("todayEmotions", emotionLogRepository.findByUserIdAndDate(userId, today));
            data.put("todayWorkouts", workoutLogRepository.findByUserIdAndDate(userId, today));
            
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error fetching user data for ID {}: ", userId, e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
    
    // 시스템 상태 확인
    @GetMapping("/status")
    public ResponseEntity<?> getSystemStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("timestamp", System.currentTimeMillis());
            status.put("userCount", userRepository.count());
            status.put("mealCount", mealLogRepository.count());
            status.put("workoutCount", workoutLogRepository.count());
            status.put("emotionCount", emotionLogRepository.count());
            status.put("status", "OK");
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error checking system status: ", e);
            return ResponseEntity.ok(Map.of(
                "status", "ERROR",
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}