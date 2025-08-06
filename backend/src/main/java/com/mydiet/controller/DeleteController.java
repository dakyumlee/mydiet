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
@RequestMapping("/api/delete")
@RequiredArgsConstructor
@Slf4j
public class DeleteController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;

    /**
     * 실제 사용자 ID 찾기
     */
    private Long getActualUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            return userId;
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return 1L;
        }

        return users.get(0).getId();
    }

    /**
     * 특정 식단 삭제
     */
    @DeleteMapping("/meal/{mealId}")
    public ResponseEntity<Map<String, Object>> deleteMeal(@PathVariable Long mealId, HttpSession session) {
        log.info("=== 식단 삭제: mealId={} ===", mealId);

        try {
            Long userId = getActualUserId(session);
            
            Optional<MealLog> mealOpt = mealLogRepository.findById(mealId);
            if (mealOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "식단을 찾을 수 없습니다"
                ));
            }

            MealLog meal = mealOpt.get();
            
            // 소유권 확인 (같은 사용자인지)
            if (!meal.getUser().getId().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "삭제 권한이 없습니다"
                ));
            }

            String description = meal.getDescription();
            mealLogRepository.delete(meal);
            
            log.info("식단 삭제 완료: {}", description);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "식단이 삭제되었습니다: " + description
            ));

        } catch (Exception e) {
            log.error("식단 삭제 실패: mealId={}", mealId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 특정 운동 삭제
     */
    @DeleteMapping("/workout/{workoutId}")
    public ResponseEntity<Map<String, Object>> deleteWorkout(@PathVariable Long workoutId, HttpSession session) {
        log.info("=== 운동 삭제: workoutId={} ===", workoutId);

        try {
            Long userId = getActualUserId(session);
            
            Optional<WorkoutLog> workoutOpt = workoutLogRepository.findById(workoutId);
            if (workoutOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "운동을 찾을 수 없습니다"
                ));
            }

            WorkoutLog workout = workoutOpt.get();
            
            // 소유권 확인
            if (!workout.getUser().getId().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "삭제 권한이 없습니다"
                ));
            }

            String type = workout.getType();
            workoutLogRepository.delete(workout);
            
            log.info("운동 삭제 완료: {}", type);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "운동이 삭제되었습니다: " + type
            ));

        } catch (Exception e) {
            log.error("운동 삭제 실패: workoutId={}", workoutId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 특정 감정 삭제
     */
    @DeleteMapping("/emotion/{emotionId}")
    public ResponseEntity<Map<String, Object>> deleteEmotion(@PathVariable Long emotionId, HttpSession session) {
        log.info("=== 감정 삭제: emotionId={} ===", emotionId);

        try {
            Long userId = getActualUserId(session);
            
            Optional<EmotionLog> emotionOpt = emotionLogRepository.findById(emotionId);
            if (emotionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "감정을 찾을 수 없습니다"
                ));
            }

            EmotionLog emotion = emotionOpt.get();
            
            // 소유권 확인
            if (!emotion.getUser().getId().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "삭제 권한이 없습니다"
                ));
            }

            String mood = emotion.getMood();
            emotionLogRepository.delete(emotion);
            
            log.info("감정 삭제 완료: {}", mood);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "감정이 삭제되었습니다: " + mood
            ));

        } catch (Exception e) {
            log.error("감정 삭제 실패: emotionId={}", emotionId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 오늘의 모든 식단 삭제
     */
    @DeleteMapping("/meals/today")
    public ResponseEntity<Map<String, Object>> deleteTodayMeals(HttpSession session) {
        log.info("=== 오늘 모든 식단 삭제 ===");

        try {
            Long userId = getActualUserId(session);
            LocalDate today = LocalDate.now();
            
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            
            if (todayMeals.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "삭제할 식단이 없습니다",
                    "deletedCount", 0
                ));
            }

            int count = todayMeals.size();
            mealLogRepository.deleteAll(todayMeals);
            
            log.info("오늘 식단 {} 개 삭제 완료", count);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "오늘의 식단 " + count + "개가 삭제되었습니다",
                "deletedCount", count
            ));

        } catch (Exception e) {
            log.error("오늘 식단 전체 삭제 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 오늘의 모든 운동 삭제
     */
    @DeleteMapping("/workouts/today")
    public ResponseEntity<Map<String, Object>> deleteTodayWorkouts(HttpSession session) {
        log.info("=== 오늘 모든 운동 삭제 ===");

        try {
            Long userId = getActualUserId(session);
            LocalDate today = LocalDate.now();
            
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            
            if (todayWorkouts.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "삭제할 운동이 없습니다",
                    "deletedCount", 0
                ));
            }

            int count = todayWorkouts.size();
            workoutLogRepository.deleteAll(todayWorkouts);
            
            log.info("오늘 운동 {} 개 삭제 완료", count);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "오늘의 운동 " + count + "개가 삭제되었습니다",
                "deletedCount", count
            ));

        } catch (Exception e) {
            log.error("오늘 운동 전체 삭제 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 오늘의 모든 감정 삭제
     */
    @DeleteMapping("/emotions/today")
    public ResponseEntity<Map<String, Object>> deleteTodayEmotions(HttpSession session) {
        log.info("=== 오늘 모든 감정 삭제 ===");

        try {
            Long userId = getActualUserId(session);
            LocalDate today = LocalDate.now();
            
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            if (todayEmotions.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "삭제할 감정이 없습니다",
                    "deletedCount", 0
                ));
            }

            int count = todayEmotions.size();
            emotionLogRepository.deleteAll(todayEmotions);
            
            log.info("오늘 감정 {} 개 삭제 완료", count);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "오늘의 감정 " + count + "개가 삭제되었습니다",
                "deletedCount", count
            ));

        } catch (Exception e) {
            log.error("오늘 감정 전체 삭제 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 오늘의 모든 데이터 삭제
     */
    @DeleteMapping("/all/today")
    public ResponseEntity<Map<String, Object>> deleteTodayAll(HttpSession session) {
        log.info("=== 오늘 모든 데이터 삭제 ===");

        try {
            Long userId = getActualUserId(session);
            LocalDate today = LocalDate.now();
            
            List<MealLog> todayMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> todayEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            int mealCount = todayMeals.size();
            int workoutCount = todayWorkouts.size();
            int emotionCount = todayEmotions.size();
            int totalCount = mealCount + workoutCount + emotionCount;

            if (totalCount == 0) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "삭제할 데이터가 없습니다",
                    "deletedCount", 0
                ));
            }

            // 모두 삭제
            mealLogRepository.deleteAll(todayMeals);
            workoutLogRepository.deleteAll(todayWorkouts);
            emotionLogRepository.deleteAll(todayEmotions);
            
            log.info("오늘 모든 데이터 삭제 완료 - 식단: {}개, 운동: {}개, 감정: {}개", 
                    mealCount, workoutCount, emotionCount);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", String.format("오늘의 모든 데이터가 삭제되었습니다 (식단: %d개, 운동: %d개, 감정: %d개)", 
                        mealCount, workoutCount, emotionCount),
                "deletedCount", totalCount,
                "mealCount", mealCount,
                "workoutCount", workoutCount,
                "emotionCount", emotionCount
            ));

        } catch (Exception e) {
            log.error("오늘 전체 데이터 삭제 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}