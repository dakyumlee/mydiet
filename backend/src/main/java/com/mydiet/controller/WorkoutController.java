package com.mydiet.controller;

import com.mydiet.dto.WorkoutRequest;
import com.mydiet.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<?> saveWorkout(@RequestBody WorkoutRequest request) {
        return ResponseEntity.ok(workoutService.saveWorkout(request));
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts(@RequestParam Long userId) {
        return ResponseEntity.ok(workoutService.getTodayWorkouts(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getWorkoutStats(@RequestParam Long userId, 
                                           @RequestParam(required = false) String period) {
        return ResponseEntity.ok(workoutService.getWorkoutStats(userId, period));
    }

    @DeleteMapping("/{workoutId}")
    public ResponseEntity<?> deleteWorkout(@PathVariable Long workoutId, 
                                         @RequestParam Long userId) {
        workoutService.deleteWorkout(workoutId, userId);
        return ResponseEntity.ok().build();
    }
}