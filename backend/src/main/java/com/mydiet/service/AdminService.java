package com.mydiet.service;

import com.mydiet.model.User;
import com.mydiet.model.ClaudeResponse;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.WorkoutLogRepository;
import com.mydiet.repository.ClaudeResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long userCount = userRepository.count();
        long mealCount = mealLogRepository.count();
        long emotionCount = emotionLogRepository.count();
        long workoutCount = workoutLogRepository.count();
        
        stats.put("totalUsers", userCount);
        stats.put("totalMeals", mealCount);
        stats.put("totalEmotions", emotionCount);
        stats.put("totalWorkouts", workoutCount);
        
        return stats;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Long getUserCount() {
        return userRepository.count();
    }

    public User getUserDetail(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        long mealCount = mealLogRepository.findAll().stream()
            .filter(meal -> meal.getUser().getId().equals(userId))
            .count();
        
        long emotionCount = emotionLogRepository.findAll().stream()
            .filter(emotion -> emotion.getUser().getId().equals(userId))
            .count();
        
        long workoutCount = workoutLogRepository.findAll().stream()
            .filter(workout -> workout.getUser().getId().equals(userId))
            .count();
        
        stats.put("mealCount", mealCount);
        stats.put("emotionCount", emotionCount);
        stats.put("workoutCount", workoutCount);
        stats.put("lastActivity", null);
        
        return stats;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        mealLogRepository.deleteAll(
            mealLogRepository.findAll().stream()
                .filter(meal -> meal.getUser().getId().equals(userId))
                .toList()
        );
        
        emotionLogRepository.deleteAll(
            emotionLogRepository.findAll().stream()
                .filter(emotion -> emotion.getUser().getId().equals(userId))
                .toList()
        );
        
        workoutLogRepository.deleteAll(
            workoutLogRepository.findAll().stream()
                .filter(workout -> workout.getUser().getId().equals(userId))
                .toList()
        );
        
        claudeResponseRepository.deleteAll(
            claudeResponseRepository.findByUserIdOrderByCreatedAtDesc(userId)
        );
        
        userRepository.delete(user);
    }

    public List<ClaudeResponse> getClaudeResponses() {
        return claudeResponseRepository.findAll();
    }

    public Map<String, Object> getUserCountStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("count", userRepository.count());
        return stats;
    }

    public Map<String, Object> getMealCountStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("count", mealLogRepository.count());
        return stats;
    }

    public Map<String, Object> getEmotionCountStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("count", emotionLogRepository.count());
        return stats;
    }

    public Map<String, Object> getWorkoutCountStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("count", workoutLogRepository.count());
        return stats;
    }
}