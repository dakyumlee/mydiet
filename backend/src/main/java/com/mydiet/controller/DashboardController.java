package com.mydiet.controller;

import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            LocalDate today = LocalDate.now();
            
            int mealCount = mealLogRepository.findByUserIdAndDate(userId, today).size();
            int workoutCount = workoutLogRepository.findByUserIdAndDate(userId, today).size();
            int emotionCount = emotionLogRepository.findByUserIdAndDate(userId, today).size();
            
            int totalCalories = mealLogRepository.findByUserIdAndDate(userId, today)
                .stream()
                .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                .sum();
            
            int burnedCalories = workoutLogRepository.findByUserIdAndDate(userId, today)
                .stream()
                .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                .sum();
            
            double goalAchievement = totalCalories > 0 ? Math.min((double) totalCalories / 2000 * 100, 100) : 0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("mealCount", mealCount);
            stats.put("workoutCount", workoutCount);
            stats.put("emotionCount", emotionCount);
            stats.put("totalCalories", totalCalories);
            stats.put("burnedCalories", burnedCalories);
            stats.put("goalAchievement", Math.round(goalAchievement));
            
            log.info("Dashboard stats for user {}: {}", userId, stats);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Error fetching dashboard stats: ", e);
            return ResponseEntity.ok(Map.of(
                "mealCount", 0,
                "workoutCount", 0,
                "emotionCount", 0,
                "totalCalories", 0,
                "burnedCalories", 0,
                "goalAchievement", 0
            ));
        }
    }
}