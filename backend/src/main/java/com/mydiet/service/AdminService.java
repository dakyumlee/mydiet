package com.mydiet.service;

import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
         
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
         
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long todayUsers = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
        stats.put("todayUsers", todayUsers);
         
        LocalDate today = LocalDate.now();
        long todayMeals = mealLogRepository.countByDate(today);
        long todayEmotions = emotionLogRepository.countByDate(today);
        long todayWorkouts = workoutLogRepository.countByDate(today);
        
        stats.put("todayMeals", todayMeals);
        stats.put("todayEmotions", todayEmotions);
        stats.put("todayWorkouts", todayWorkouts);
         
        long totalClaudeResponses = claudeResponseRepository.count();
        stats.put("totalClaudeResponses", totalClaudeResponses);
         
        List<Map<String, Object>> weeklyStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            
            long count = userRepository.countByCreatedAtBetween(start, end);
            
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", date.toString());
            dayStats.put("users", count);
            weeklyStats.add(dayStats);
        }
        stats.put("weeklyStats", weeklyStats);
        
        return stats;
    }
    
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("nickname", user.getNickname());
                userMap.put("email", user.getEmail());
                userMap.put("weightGoal", user.getWeightGoal());
                userMap.put("emotionMode", user.getEmotionMode());
                userMap.put("provider", user.getProvider());
                userMap.put("createdAt", user.getCreatedAt());
                userMap.put("updatedAt", user.getUpdatedAt());
                return userMap;
            }).collect(Collectors.toList());  
    }
}