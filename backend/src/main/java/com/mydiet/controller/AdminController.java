package com.mydiet.controller;

import com.mydiet.repository.*;
import com.mydiet.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalUsers = userRepository.count();
            long totalMeals = mealLogRepository.count();
            long totalWorkouts = workoutLogRepository.count();
            long totalEmotions = emotionLogRepository.count();
            
            LocalDate today = LocalDate.now();
            
            Long todayMeals = 0L;
            Long todayWorkouts = 0L;
            Long todayEmotions = 0L;
            Long activeUsers = 0L;
            
            try {
                todayMeals = (Long) entityManager
                    .createQuery("SELECT COUNT(m) FROM MealLog m WHERE m.date = :today")
                    .setParameter("today", today)
                    .getSingleResult();
            } catch (Exception e) {
                log.error("Error counting today meals: ", e);
                todayMeals = 0L;
            }
            
            try {
                todayWorkouts = (Long) entityManager
                    .createQuery("SELECT COUNT(w) FROM WorkoutLog w WHERE w.date = :today")
                    .setParameter("today", today)
                    .getSingleResult();
            } catch (Exception e) {
                log.error("Error counting today workouts: ", e);
                todayWorkouts = 0L;
            }
            
            try {
                todayEmotions = (Long) entityManager
                    .createQuery("SELECT COUNT(e) FROM EmotionLog e WHERE e.date = :today")
                    .setParameter("today", today)
                    .getSingleResult();
            } catch (Exception e) {
                log.error("Error counting today emotions: ", e);
                todayEmotions = 0L;
            }
            
            try {
                activeUsers = (Long) entityManager
                    .createQuery("SELECT COUNT(u) FROM User u WHERE DATE(u.lastLoginAt) = :today")
                    .setParameter("today", today)
                    .getSingleResult();
            } catch (Exception e) {
                log.error("Error counting active users: ", e);
                activeUsers = 0L;
            }
            
            stats.put("totalUsers", totalUsers);
            stats.put("totalMeals", totalMeals);
            stats.put("totalWorkouts", totalWorkouts);
            stats.put("totalEmotions", totalEmotions);
            stats.put("activeUsers", activeUsers);
            stats.put("todayMeals", todayMeals);
            stats.put("todayWorkouts", todayWorkouts);
            stats.put("todayEmotions", todayEmotions);
            
            log.info("Admin stats: totalUsers={}, totalMeals={}, totalWorkouts={}, totalEmotions={}, todayMeals={}, todayWorkouts={}, todayEmotions={}", 
                totalUsers, totalMeals, totalWorkouts, totalEmotions, todayMeals, todayWorkouts, todayEmotions);
            
        } catch (Exception e) {
            log.error("Error in getStats: ", e);
            stats.put("totalUsers", 0L);
            stats.put("totalMeals", 0L);
            stats.put("totalWorkouts", 0L);
            stats.put("totalEmotions", 0L);
            stats.put("activeUsers", 0L);
            stats.put("todayMeals", 0L);
            stats.put("todayWorkouts", 0L);
            stats.put("todayEmotions", 0L);
        }
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = userRepository.findAll();
            log.info("Found {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error getting users: ", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials) {
        String password = credentials.get("password");
        
        if ("admin1234".equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "관리자 로그인 성공");
            
            log.info("Admin login successful");
            return ResponseEntity.ok(response);
        }
        
        log.warn("Admin login failed - wrong password");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "비밀번호가 틀렸습니다");
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<User> users = userRepository.findAll();
            result.put("users", users.size());
            
            List<?> meals = mealLogRepository.findAll();
            result.put("meals", meals.size());
            
            List<?> workouts = workoutLogRepository.findAll();
            result.put("workouts", workouts.size());
            
            List<?> emotions = emotionLogRepository.findAll();
            result.put("emotions", emotions.size());
            
            result.put("status", "Database connected");
            
        } catch (Exception e) {
            result.put("status", "Database error");
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
}