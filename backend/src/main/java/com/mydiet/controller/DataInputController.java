package com.mydiet.controller;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataInputController {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    
    // 사용자 생성 또는 조회 헬퍼 메소드
    private User getOrCreateUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            user = new User();
            user.setId(userId);
            user.setNickname("사용자" + userId);
            user.setEmail("user" + userId + "@mydiet.com");
            user.setWeightGoal(65.0);
            user.setEmotionMode("보통");
            user.setCreatedAt(LocalDateTime.now());
            user = userRepository.save(user);
            log.info("Created new user with ID: {}", userId);
        }
        return user;
    }
    
    // 식단 기록
    @PostMapping("/meals")
    public ResponseEntity<?> saveMeal(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("식단 기록 요청: {}", request);
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            User user = getOrCreateUser(userId);
            
            MealLog meal = new MealLog();
            meal.setUser(user);
            meal.setDescription((String) request.getOrDefault("description", "식단"));
            
            // 칼로리 처리
            Object caloriesObj = request.get("calories");
            if (caloriesObj == null) caloriesObj = request.get("caloriesEstimate");
            
            Integer calories = 0;
            if (caloriesObj != null) {
                if (caloriesObj instanceof Number) {
                    calories = ((Number) caloriesObj).intValue();
                } else {
                    try {
                        calories = Integer.valueOf(caloriesObj.toString());
                    } catch (NumberFormatException e) {
                        calories = 0;
                    }
                }
            }
            meal.setCaloriesEstimate(calories);
            
            // 사진 처리
            if (request.containsKey("photoData")) {
                String photoData = (String) request.get("photoData");
                if (photoData != null && photoData.length() > 1000000) {
                    log.warn("사진 데이터가 너무 큼, 건너뜀");
                    meal.setPhotoUrl(null);
                } else {
                    meal.setPhotoUrl(photoData);
                }
            }
            
            meal.setDate(LocalDate.now());
            
            MealLog saved = mealLogRepository.save(meal);
            log.info("식단 저장 완료 ID: {}", saved.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "식단이 저장되었습니다!",
                "meal", saved
            ));
            
        } catch (Exception e) {
            log.error("식단 저장 중 오류: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // 운동 기록
    @PostMapping("/workouts")
    public ResponseEntity<?> saveWorkout(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("운동 기록 요청: {}", request);
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            User user = getOrCreateUser(userId);
            
            WorkoutLog workout = new WorkoutLog();
            workout.setUser(user);
            workout.setType((String) request.getOrDefault("type", "운동"));
            
            // 시간 처리
            Object durationObj = request.get("duration");
            Integer duration = 0;
            if (durationObj != null) {
                if (durationObj instanceof Number) {
                    duration = ((Number) durationObj).intValue();
                } else {
                    try {
                        duration = Integer.valueOf(durationObj.toString());
                    } catch (NumberFormatException e) {
                        duration = 0;
                    }
                }
            }
            workout.setDuration(duration);
            
            // 칼로리 소모 처리
            Object burnedObj = request.get("caloriesBurned");
            if (burnedObj == null) burnedObj = request.get("calories");
            
            Integer caloriesBurned = 0;
            if (burnedObj != null) {
                if (burnedObj instanceof Number) {
                    caloriesBurned = ((Number) burnedObj).intValue();
                } else {
                    try {
                        caloriesBurned = Integer.valueOf(burnedObj.toString());
                    } catch (NumberFormatException e) {
                        caloriesBurned = 0;
                    }
                }
            }
            workout.setCaloriesBurned(caloriesBurned);
            
            workout.setDate(LocalDate.now());
            
            WorkoutLog saved = workoutLogRepository.save(workout);
            log.info("운동 저장 완료 ID: {}", saved.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "운동이 기록되었습니다!",
                "workout", saved
            ));
            
        } catch (Exception e) {
            log.error("운동 저장 중 오류: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // 감정 기록
    @PostMapping("/emotions")
    public ResponseEntity<?> saveEmotion(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("감정 기록 요청: {}", request);
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            User user = getOrCreateUser(userId);
            
            EmotionLog emotion = new EmotionLog();
            emotion.setUser(user);
            emotion.setMood((String) request.getOrDefault("mood", "보통"));
            emotion.setNote((String) request.getOrDefault("note", ""));
            emotion.setDate(LocalDate.now());
            
            EmotionLog saved = emotionLogRepository.save(emotion);
            log.info("감정 저장 완료 ID: {}", saved.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "감정이 기록되었습니다!",
                "emotion", saved
            ));
            
        } catch (Exception e) {
            log.error("감정 저장 중 오류: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // 오늘 데이터 조회
    @GetMapping("/meals/today")
    public ResponseEntity<?> getTodayMeals(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            return ResponseEntity.ok(mealLogRepository.findByUserIdAndDate(userId, LocalDate.now()));
        } catch (Exception e) {
            log.error("오늘 식단 조회 오류: ", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/workouts/today")
    public ResponseEntity<?> getTodayWorkouts(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            return ResponseEntity.ok(workoutLogRepository.findByUserIdAndDate(userId, LocalDate.now()));
        } catch (Exception e) {
            log.error("오늘 운동 조회 오류: ", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/emotions/today")
    public ResponseEntity<?> getTodayEmotions(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            return ResponseEntity.ok(emotionLogRepository.findByUserIdAndDate(userId, LocalDate.now()));
        } catch (Exception e) {
            log.error("오늘 감정 조회 오류: ", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
}