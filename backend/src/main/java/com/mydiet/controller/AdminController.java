package com.mydiet.controller;

import com.mydiet.dto.StatsResponse;
import com.mydiet.dto.UserStatsResponse;
import com.mydiet.model.*;
import com.mydiet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;
    
    @GetMapping("/stats/users")
    public ResponseEntity<StatsResponse> getUserStats() {
        long count = userRepository.count();
        log.info("사용자 통계 조회: {}", count);
        return ResponseEntity.ok(new StatsResponse(count));
    }
    
    @GetMapping("/stats/meals")
    public ResponseEntity<StatsResponse> getMealStats() {
        long count = mealLogRepository.count();
        return ResponseEntity.ok(new StatsResponse(count));
    }
    
    @GetMapping("/stats/emotions")
    public ResponseEntity<StatsResponse> getEmotionStats() {
        long count = emotionLogRepository.count();
        return ResponseEntity.ok(new StatsResponse(count));
    }
    
    @GetMapping("/stats/workouts")
    public ResponseEntity<StatsResponse> getWorkoutStats() {
        long count = workoutLogRepository.count();
        return ResponseEntity.ok(new StatsResponse(count));
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("전체 사용자 조회: {}명", users.size());
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/users/{id}/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Long id) {
        long mealCount = mealLogRepository.countByUserId(id);
        long emotionCount = emotionLogRepository.countByUserId(id);
        long workoutCount = workoutLogRepository.countByUserId(id);
        
        LocalDateTime lastActivity = LocalDateTime.now();
        
        UserStatsResponse stats = new UserStatsResponse(mealCount, emotionCount, workoutCount, lastActivity);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/claude-responses")
    public ResponseEntity<List<ClaudeResponse>> getClaudeResponses() {
        List<ClaudeResponse> responses = claudeResponseRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(responses);
    }
}