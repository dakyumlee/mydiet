package com.mydiet.controller;

import com.mydiet.model.WorkoutLog;
import com.mydiet.model.User;
import com.mydiet.repository.WorkoutLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkoutController {
    
    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> saveWorkout(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("Saving workout: {}", request);
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            User user = userRepository.findById(userId).orElseGet(() -> {
                User newUser = new User();
                newUser.setId(userId);
                newUser.setEmail("user" + userId + "@mydiet.com");
                newUser.setNickname("사용자" + userId);
                return userRepository.save(newUser);
            });
            
            WorkoutLog workout = new WorkoutLog();
            workout.setUser(user);
            workout.setType((String) request.get("type"));
            workout.setDuration(Integer.valueOf(request.get("duration").toString()));
            workout.setCaloriesBurned(Integer.valueOf(request.getOrDefault("caloriesBurned", 0).toString()));
            workout.setDate(LocalDate.now());
            
            WorkoutLog saved = workoutLogRepository.save(workout);
            log.info("Workout saved with id: {}", saved.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "운동이 기록되었습니다!"
            ));
            
        } catch (Exception e) {
            log.error("Error saving workout: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(userId, LocalDate.now());
            return ResponseEntity.ok(workouts);
        } catch (Exception e) {
            log.error("Error fetching workouts: ", e);
            return ResponseEntity.ok(List.of());
        }
    }
}