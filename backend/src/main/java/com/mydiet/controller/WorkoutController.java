package com.mydiet.controller;

import com.mydiet.dto.WorkoutRequest;
import com.mydiet.model.WorkoutLog;
import com.mydiet.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<?> saveWorkout(@RequestBody WorkoutRequest request, Authentication authentication) {
        try { 
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Authentication required");
            }
             
            Long actualUserId = request.getUserId() != null ? request.getUserId() : 1L;
            request.setUserId(actualUserId);
            
            WorkoutLog saved = workoutService.saveWorkout(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving workout: " + e.getMessage());
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts(@RequestParam(required = false) Long userId, 
                                            Authentication authentication) {
        try { 
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(Collections.emptyList());  
            }
             
            Long actualUserId = userId != null ? userId : 1L;
            
            return ResponseEntity.ok(workoutService.getTodayWorkouts(actualUserId));
        } catch (Exception e) { 
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}