package com.mydiet.controller;

import com.mydiet.model.WorkoutLog;
import com.mydiet.model.User;
import com.mydiet.repository.WorkoutLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.service.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {
    
    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> saveWorkout(@AuthenticationPrincipal OAuth2UserPrincipal principal,
                                         @RequestBody Map<String, Object> request) {
        try {
            User user = userRepository.findById(principal.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            WorkoutLog workout = new WorkoutLog();
            workout.setUser(user);
            workout.setType((String) request.get("type"));
            workout.setDuration((Integer) request.get("duration"));
            workout.setCaloriesBurned((Integer) request.get("caloriesBurned"));
            workout.setDate(LocalDate.now());
            
            WorkoutLog saved = workoutLogRepository.save(workout);
            log.info("Workout saved for user {}: {}", user.getEmail(), saved.getType());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "운동이 기록되었습니다."
            ));
        } catch (Exception e) {
            log.error("Error saving workout: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        try {
            List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(
                principal.getUserId(), 
                LocalDate.now()
            );
            return ResponseEntity.ok(workouts);
        } catch (Exception e) {
            log.error("Error fetching workouts: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}