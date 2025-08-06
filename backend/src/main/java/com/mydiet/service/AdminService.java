package com.mydiet.service;

import com.mydiet.repository.*;
import com.mydiet.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalUsers = userRepository.count();
            
            LocalDate today = LocalDate.now();
            LocalDateTime todayStart = today.atStartOfDay();
            long activeUsers = userRepository.countByLastLoginAtAfter(todayStart);
            
            long todayMeals = mealLogRepository.countByDate(today);
            long todayWorkouts = workoutLogRepository.countByDate(today);
            long todayEmotions = emotionLogRepository.countByDate(today);
            
            long totalMeals = mealLogRepository.count();
            long totalWorkouts = workoutLogRepository.count();
            long totalEmotions = emotionLogRepository.count();
            
            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("todayMeals", todayMeals);
            stats.put("todayWorkouts", todayWorkouts);
            stats.put("todayEmotions", todayEmotions);
            stats.put("totalMeals", totalMeals);
            stats.put("totalWorkouts", totalWorkouts);
            stats.put("totalEmotions", totalEmotions);
            
            log.info("Admin stats loaded: {}", stats);
            
        } catch (Exception e) {
            log.error("Error loading admin stats: ", e);
        }
        
        return stats;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> userStats = new HashMap<>();
        
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                userStats.put("user", user);
                
                long userMeals = mealLogRepository.countByUserId(userId);
                long userWorkouts = workoutLogRepository.countByUserId(userId);
                long userEmotions = emotionLogRepository.countByUserId(userId);
                
                userStats.put("totalMeals", userMeals);
                userStats.put("totalWorkouts", userWorkouts);
                userStats.put("totalEmotions", userEmotions);
            }
        } catch (Exception e) {
            log.error("Error loading user stats for userId {}: ", userId, e);
        }
        
        return userStats;
    }
}