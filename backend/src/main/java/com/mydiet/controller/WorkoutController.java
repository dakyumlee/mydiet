package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.model.WorkoutLog;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> saveWorkout(@RequestBody WorkoutRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        WorkoutLog workout = new WorkoutLog();
        workout.setUser(user);
        workout.setType(request.getType());
        workout.setDuration(request.getDuration());
        workout.setCaloriesBurned(request.getCaloriesBurned());
        workout.setDate(LocalDate.now());
        
        workoutLogRepository.save(workout);
        return ResponseEntity.ok("운동이 저장되었습니다!");
    }

    @GetMapping("/today")
    public ResponseEntity<List<WorkoutLog>> getTodayWorkouts(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<WorkoutLog> workouts = workoutLogRepository.findByUserIdAndDate(user.getId(), LocalDate.now());
        return ResponseEntity.ok(workouts);
    }

    private User getCurrentUser(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String oauthId = String.valueOf(oAuth2User.getAttributes().get("id"));
        String userIdentifier = email != null ? email : oauthId;
        
        return userRepository.findByEmail(userIdentifier)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    public static class WorkoutRequest {
        private String type;
        private Integer duration;
        private Integer caloriesBurned;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public Integer getCaloriesBurned() { return caloriesBurned; }
        public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    }
}