package com.mydiet.controller;

import com.mydiet.model.*;
import com.mydiet.repository.*;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataRecordingController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;


    @PostMapping("/meal")
    public ResponseEntity<Map<String, Object>> saveMeal(
        @RequestBody Map<String, Object> request,
        HttpSession session) {
        
        log.info("=== 식사 기록 저장 요청 ===");
        
        Long userId = (Long) session.getAttribute("userId");
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        
        if (!Boolean.TRUE.equals(authenticated) || userId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();
            
            MealLog meal = MealLog.builder()
                .user(user)
                .description((String) request.get("description"))
                .caloriesEstimate(request.get("calories") != null ? 
                    Integer.valueOf(request.get("calories").toString()) : null)
                .photoUrl((String) request.get("photoUrl"))
                .date(LocalDate.now())
                .build();
            
            MealLog savedMeal = mealLogRepository.save(meal);
            
            log.info("✅ 식사 기록 저장 완료: userId={}, 음식={}", userId, meal.getDescription());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "식사 기록이 저장되었습니다.",
                "data", savedMeal
            ));
            
        } catch (Exception e) {
            log.error("❌ 식사 기록 저장 실패", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "식사 기록 저장에 실패했습니다."));
        }
    }


    @PostMapping("/workout")
    public ResponseEntity<Map<String, Object>> saveWorkout(
        @RequestBody Map<String, Object> request,
        HttpSession session) {
        
        log.info("=== 운동 기록 저장 요청 ===");
        
        Long userId = (Long) session.getAttribute("userId");
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        
        if (!Boolean.TRUE.equals(authenticated) || userId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();
            
            WorkoutLog workout = WorkoutLog.builder()
                .user(user)
                .type((String) request.get("type"))
                .duration(request.get("duration") != null ? 
                    Integer.valueOf(request.get("duration").toString()) : null)
                .caloriesBurned(request.get("calories") != null ? 
                    Integer.valueOf(request.get("calories").toString()) : null)
                .date(LocalDate.now())
                .build();
            
            WorkoutLog savedWorkout = workoutLogRepository.save(workout);
            
            log.info("✅ 운동 기록 저장 완료: userId={}, 운동={}", userId, workout.getType());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "운동 기록이 저장되었습니다.",
                "data", savedWorkout
            ));
            
        } catch (Exception e) {
            log.error("❌ 운동 기록 저장 실패", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "운동 기록 저장에 실패했습니다."));
        }
    }


    @PostMapping("/emotion")
    public ResponseEntity<Map<String, Object>> saveEmotion(
        @RequestBody Map<String, Object> request,
        HttpSession session) {
        
        log.info("=== 감정 기록 저장 요청 ===");
        
        Long userId = (Long) session.getAttribute("userId");
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        
        if (!Boolean.TRUE.equals(authenticated) || userId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();
            
            EmotionLog emotion = EmotionLog.builder()
                .user(user)
                .mood((String) request.get("mood"))
                .note((String) request.get("note"))
                .date(LocalDate.now())
                .build();
            
            EmotionLog savedEmotion = emotionLogRepository.save(emotion);
            
            log.info("✅ 감정 기록 저장 완료: userId={}, 기분={}", userId, emotion.getMood());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "감정 기록이 저장되었습니다.",
                "data", savedEmotion
            ));
            
        } catch (Exception e) {
            log.error("❌ 감정 기록 저장 실패", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "감정 기록 저장에 실패했습니다."));
        }
    }


    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayData(HttpSession session) {
        
        log.info("=== 오늘 데이터 조회 요청 ===");
        
        Long userId = (Long) session.getAttribute("userId");
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        
        if (!Boolean.TRUE.equals(authenticated) || userId == null) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            LocalDate today = LocalDate.now();
            
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            Map<String, Object> todayData = new HashMap<>();
            todayData.put("meals", meals);
            todayData.put("workouts", workouts);
            todayData.put("emotions", emotions);
            todayData.put("date", today);
            
            log.info("✅ 오늘 데이터 조회 완료: 식사 {}개, 운동 {}개, 감정 {}개", 
                    meals.size(), workouts.size(), emotions.size());
            
            return ResponseEntity.ok(todayData);
            
        } catch (Exception e) {
            log.error("❌ 오늘 데이터 조회 실패", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "데이터 조회에 실패했습니다."));
        }
    }
}