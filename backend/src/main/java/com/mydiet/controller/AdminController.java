package com.mydiet.controller;

import com.mydiet.repository.*;
import com.mydiet.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Long> stats = new HashMap<>();
        
        try {
            stats.put("totalUsers", userRepository.count());
            stats.put("totalMeals", mealLogRepository.count());
            stats.put("totalWorkouts", workoutLogRepository.count());
            stats.put("totalEmotions", emotionLogRepository.count());
            stats.put("activeUsers", 0L);
            stats.put("todayMeals", 0L);
            stats.put("todayWorkouts", 0L);
            stats.put("todayEmotions", 0L);
        } catch (Exception e) {
            log.error("Stats error: ", e);
            stats.put("totalUsers", 0L);
            stats.put("totalMeals", 0L);
            stats.put("totalWorkouts", 0L);
            stats.put("totalEmotions", 0L);
            stats.put("activeUsers", 0L);
            stats.put("todayMeals", 0L);
            stats.put("todayWorkouts", 0L);
            stats.put("todayEmotions", 0L);
        }
        
        log.info("Returning stats: {}", stats);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error getting users: ", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials) {
        String password = credentials.get("password");
        
        if ("oicrcutie1998".equals(password)) {
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
}