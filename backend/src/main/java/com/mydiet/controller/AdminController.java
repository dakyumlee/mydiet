package com.mydiet.controller;

import com.mydiet.model.ClaudeResponse;
import com.mydiet.model.User;
import com.mydiet.repository.ClaudeResponseRepository;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Slf4j
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
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
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
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        try {
            log.info("사용자 삭제 요청 - ID: {}", userId);
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.notFound().body(error);
            }
            
            User user = userOpt.get();
            String userName = user.getNickname();
            
            long mealCount = mealLogRepository.countByUserId(userId);
            long workoutCount = workoutLogRepository.countByUserId(userId);
            long emotionCount = emotionLogRepository.countByUserId(userId);
            long claudeCount = claudeResponseRepository.countByUserId(userId);
            
            log.info("삭제할 데이터 - 식사: {}, 운동: {}, 감정: {}, Claude: {}", 
                    mealCount, workoutCount, emotionCount, claudeCount);
            
            mealLogRepository.deleteByUserId(userId);
            workoutLogRepository.deleteByUserId(userId);
            emotionLogRepository.deleteByUserId(userId);
            claudeResponseRepository.deleteByUserId(userId);
            
            userRepository.deleteById(userId);
            
            log.info("사용자 삭제 완료 - {}", userName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("사용자 '%s'와 관련 데이터가 모두 삭제되었습니다.", userName));
            response.put("deletedData", Map.of(
                "meals", mealCount,
                "workouts", workoutCount,
                "emotions", emotionCount,
                "claudeResponses", claudeCount
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("사용자 삭제 중 오류 발생 - ID: {}, Error: {}", userId, e.getMessage(), e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "사용자 삭제 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/users/{userId}/restore")
    public ResponseEntity<Map<String, Object>> restoreUser(@PathVariable Long userId) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "복구 기능은 아직 구현되지 않았습니다.");
        return ResponseEntity.notImplemented().body(response);
    }

    private String getActivityStatus(User user) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        boolean hasRecentActivity = claudeResponseRepository.existsByUserIdAndCreatedAtAfter(user.getId(), weekAgo);
        return hasRecentActivity ? "active" : "inactive";
    }
}