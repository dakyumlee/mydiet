package com.mydiet.controller;

import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

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
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}