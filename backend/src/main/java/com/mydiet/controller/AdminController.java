package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.model.MealLog;
import com.mydiet.model.WorkoutLog;
import com.mydiet.model.EmotionLog;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.WorkoutLogRepository;
import com.mydiet.repository.EmotionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;
     
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
             
            long totalUsers = userRepository.count();
            long totalMeals = mealLogRepository.count();
            long totalWorkouts = workoutLogRepository.count();
            long totalEmotions = emotionLogRepository.count();
             
            LocalDate today = LocalDate.now();
            List<MealLog> todayMeals = mealLogRepository.findAll().stream()
                .filter(meal -> today.equals(meal.getDate()))
                .toList();
            List<WorkoutLog> todayWorkouts = workoutLogRepository.findAll().stream()
                .filter(workout -> today.equals(workout.getDate()))
                .toList();
            List<EmotionLog> todayEmotions = emotionLogRepository.findAll().stream()
                .filter(emotion -> today.equals(emotion.getDate()))
                .toList();
            
            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", totalUsers);
            stats.put("totalMeals", totalMeals);
            stats.put("totalWorkouts", totalWorkouts);
            stats.put("totalEmotions", totalEmotions);
            stats.put("todayMeals", todayMeals.size());
            stats.put("todayWorkouts", todayWorkouts.size());
            stats.put("todayEmotions", todayEmotions.size());
            
            log.info("Admin stats requested: {}", stats);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error fetching admin stats: ", e);
            return ResponseEntity.ok(Map.of(
                "error", e.getMessage(),
                "totalUsers", 0,
                "activeUsers", 0,
                "totalMeals", 0,
                "totalWorkouts", 0,
                "totalEmotions", 0,
                "todayMeals", 0,
                "todayWorkouts", 0,
                "todayEmotions", 0
            ));
        }
    }
     
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            log.info("Admin users list requested: {} users found", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching users: ", e);
            return ResponseEntity.ok(List.of());
        }
    }
     
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.ok(Map.of("error", "사용자를 찾을 수 없습니다"));
            }
            
            LocalDate today = LocalDate.now();
            List<MealLog> userMeals = mealLogRepository.findByUserIdAndDate(userId, today);
            List<WorkoutLog> userWorkouts = workoutLogRepository.findByUserIdAndDate(userId, today);
            List<EmotionLog> userEmotions = emotionLogRepository.findByUserIdAndDate(userId, today);
            
            Map<String, Object> userDetail = new HashMap<>();
            userDetail.put("user", user);
            userDetail.put("todayMeals", userMeals);
            userDetail.put("todayWorkouts", userWorkouts);
            userDetail.put("todayEmotions", userEmotions);
             
            List<MealLog> allUserMeals = mealLogRepository.findAll().stream()
                .filter(meal -> userId.equals(meal.getUser().getId()))
                .toList();
            List<WorkoutLog> allUserWorkouts = workoutLogRepository.findAll().stream()
                .filter(workout -> userId.equals(workout.getUser().getId()))
                .toList();
            List<EmotionLog> allUserEmotions = emotionLogRepository.findAll().stream()
                .filter(emotion -> userId.equals(emotion.getUser().getId()))
                .toList();
            
            userDetail.put("totalMeals", allUserMeals.size());
            userDetail.put("totalWorkouts", allUserWorkouts.size());
            userDetail.put("totalEmotions", allUserEmotions.size());
            
            log.info("User detail requested for ID: {}", userId);
            return ResponseEntity.ok(userDetail);
            
        } catch (Exception e) {
            log.error("Error fetching user detail for ID {}: ", userId, e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
     
    @DeleteMapping("/users/{userId}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "사용자를 찾을 수 없습니다"
                ));
            }
             
            List<MealLog> userMeals = mealLogRepository.findAll().stream()
                .filter(meal -> userId.equals(meal.getUser().getId()))
                .toList();
            mealLogRepository.deleteAll(userMeals);
             
            List<WorkoutLog> userWorkouts = workoutLogRepository.findAll().stream()
                .filter(workout -> userId.equals(workout.getUser().getId()))
                .toList();
            workoutLogRepository.deleteAll(userWorkouts);
             
            List<EmotionLog> userEmotions = emotionLogRepository.findAll().stream()
                .filter(emotion -> userId.equals(emotion.getUser().getId()))
                .toList();
            emotionLogRepository.deleteAll(userEmotions);
             
            userRepository.delete(user);
            
            log.info("User deleted: ID={}, nickname={}", userId, user.getNickname());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "사용자와 관련된 모든 데이터가 삭제되었습니다",
                "deletedUserId", userId,
                "deletedUserNickname", user.getNickname()
            ));
            
        } catch (Exception e) {
            log.error("Error deleting user ID {}: ", userId, e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "사용자 삭제 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
     
    @GetMapping("/test")
    public ResponseEntity<?> testDatabase() {
        try {
            Map<String, Object> testResult = new HashMap<>();
            
            testResult.put("users", userRepository.count());
            testResult.put("meals", mealLogRepository.count());
            testResult.put("workouts", workoutLogRepository.count());
            testResult.put("emotions", emotionLogRepository.count());
            testResult.put("status", "OK");
            testResult.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(testResult);
            
        } catch (Exception e) {
            log.error("Database test error: ", e);
            return ResponseEntity.ok(Map.of(
                "status", "ERROR",
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
     
    @GetMapping("/system")
    public ResponseEntity<?> getSystemInfo() {
        try {
            Map<String, Object> systemInfo = new HashMap<>();
            
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            systemInfo.put("maxMemory", maxMemory / 1024 / 1024 + "MB");
            systemInfo.put("totalMemory", totalMemory / 1024 / 1024 + "MB");
            systemInfo.put("usedMemory", usedMemory / 1024 / 1024 + "MB");
            systemInfo.put("freeMemory", freeMemory / 1024 / 1024 + "MB");
            systemInfo.put("processors", runtime.availableProcessors());
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(systemInfo);
            
        } catch (Exception e) {
            log.error("Error fetching system info: ", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
}