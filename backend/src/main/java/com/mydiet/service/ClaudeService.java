package com.mydiet.service;

import com.mydiet.entity.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaudeService {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;

    public String generateResponse(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        LocalDate today = LocalDate.now();

        List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, today);
        List<EmotionLog> emotions = emotionLogRepository.findByUserIdAndDate(userId, today);
        List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, today);

        String response = buildMockResponse(user, meals, emotions, workouts);

        ClaudeResponse log = ClaudeResponse.builder()
                .user(user)
                .type("daily")
                .content(response)
                .createdAt(LocalDateTime.now())
                .build();
        claudeResponseRepository.save(log);

        return response;
    }

    private String buildMockResponse(User user, List<MealLog> meals, List<EmotionLog> emotions, List<WorkoutLog> workouts) {
        StringBuilder response = new StringBuilder();
        
        response.append(user.getNickname()).append("님, ");
        
        if (meals.isEmpty() && workouts.isEmpty()) {
            response.append("오늘은 기록이 없네요! 식단과 운동을 기록해보세요! 💪");
        } else {
            response.append("오늘도 열심히 하고 계시네요! ");
            
            if (!meals.isEmpty()) {
                int totalCalories = meals.stream()
                    .mapToInt(meal -> meal.getCaloriesEstimate() != null ? meal.getCaloriesEstimate() : 0)
                    .sum();
                response.append("총 ").append(totalCalories).append("kcal 섭취했네요. ");
            }
            
            if (!workouts.isEmpty()) {
                int totalBurned = workouts.stream()
                    .mapToInt(workout -> workout.getCaloriesBurned() != null ? workout.getCaloriesBurned() : 0)
                    .sum();
                response.append("운동으로 ").append(totalBurned).append("kcal 소모했어요! ");
            }
            
            response.append("목표까지 화이팅! 🔥");
        }
        
        return response.toString();
    }
}