package com.mydiet.controller;

import com.mydiet.model.ClaudeResponse;
import com.mydiet.model.User;
import com.mydiet.repository.ClaudeResponseRepository;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long totalMessages = claudeResponseRepository.count();
        long activeUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(7));
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalMessages", totalMessages);
        stats.put("activeUsers", activeUsers);
        stats.put("goalAchievementRate", 73);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> userList = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("nickname", user.getNickname());
            userMap.put("email", user.getEmail());
            userMap.put("weightGoal", user.getWeightGoal());
            userMap.put("emotionMode", user.getEmotionMode());
            userMap.put("createdAt", user.getCreatedAt().toLocalDate().toString());
            userMap.put("status", getActivityStatus(user));
            return userMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/users/{userId}/detail")
    public ResponseEntity<Map<String, Object>> getUserDetail(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        Map<String, Object> userDetail = new HashMap<>();
        userDetail.put("id", user.getId());
        userDetail.put("nickname", user.getNickname());
        userDetail.put("email", user.getEmail());
        userDetail.put("weightGoal", user.getWeightGoal());
        userDetail.put("emotionMode", user.getEmotionMode());
        
        long totalMeals = mealLogRepository.countByUserId(userId);
        long totalWorkouts = workoutLogRepository.countByUserId(userId);
        long totalEmotions = emotionLogRepository.countByUserId(userId);
        long aiChats = claudeResponseRepository.countByUserId(userId);
        
        userDetail.put("totalMeals", totalMeals);
        userDetail.put("totalWorkouts", totalWorkouts);
        userDetail.put("totalEmotions", totalEmotions);
        userDetail.put("aiChats", aiChats);
        
        return ResponseEntity.ok(userDetail);
    }

    @GetMapping("/users/{userId}/conversations")
    public ResponseEntity<List<Map<String, Object>>> getUserConversations(@PathVariable Long userId) {
        List<ClaudeResponse> conversations = claudeResponseRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        List<Map<String, Object>> conversationList = conversations.stream().map(conv -> {
            Map<String, Object> convMap = new HashMap<>();
            convMap.put("id", conv.getId());
            convMap.put("type", conv.getType());
            convMap.put("content", conv.getContent());
            convMap.put("createdAt", conv.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            return convMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(conversationList);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<Map<String, Object>>> getAllConversations() {
        List<ClaudeResponse> conversations = claudeResponseRepository.findAllByOrderByCreatedAtDesc();
        
        List<Map<String, Object>> conversationList = conversations.stream().map(conv -> {
            Map<String, Object> convMap = new HashMap<>();
            convMap.put("id", conv.getId());
            convMap.put("userId", conv.getUser().getId());
            convMap.put("userName", conv.getUser().getNickname());
            convMap.put("type", conv.getType());
            convMap.put("content", conv.getContent());
            convMap.put("createdAt", conv.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            return convMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(conversationList);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "사용자가 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    private String getActivityStatus(User user) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        boolean hasRecentActivity = claudeResponseRepository.existsByUserIdAndCreatedAtAfter(user.getId(), weekAgo);
        return hasRecentActivity ? "active" : "inactive";
    }
}