package com.mydiet.service;

import com.mydiet.dto.StatisticsResponse;
import com.mydiet.dto.TodayStatsResponse;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final EmotionLogRepository emotionLogRepository;

    public StatisticsResponse getStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByLastLoginAtAfter(LocalDateTime.now().minusDays(7));
        long totalMeals = mealLogRepository.count();
        long totalWorkouts = workoutLogRepository.count();
        
        return StatisticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalMeals(totalMeals)
                .totalWorkouts(totalWorkouts)
                .build();
    }

    public TodayStatsResponse getTodayStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        long signups = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
        long meals = mealLogRepository.countByDate(today);
        long workouts = workoutLogRepository.countByDate(today);
        
        return TodayStatsResponse.builder()
                .signups(signups)
                .logins(0)
                .meals(meals)
                .workouts(workouts)
                .build();
    }
}