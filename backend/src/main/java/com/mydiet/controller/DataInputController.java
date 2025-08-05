package com.mydiet.controller;

import com.mydiet.model.EmotionLog;
import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.model.WorkoutLog;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataInputController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final WorkoutLogRepository workoutLogRepository;

    @PostMapping("/meal")
    public ResponseEntity<String> saveMeal(@RequestBody MealRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        MealLog meal = new MealLog();
        meal.setUser(user);
        meal.setDescription(request.getDescription());
        meal.setCaloriesEstimate(request.getCaloriesEstimate());
        meal.setDate(LocalDate.now());
        
        mealLogRepository.save(meal);
        return ResponseEntity.ok("식단이 저장되었습니다!");
    }

    @PostMapping("/emotion")
    public ResponseEntity<String> saveEmotion(@RequestBody EmotionRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        EmotionLog emotion = new EmotionLog();
        emotion.setUser(user);
        emotion.setMood(request.getMood());
        emotion.setNote(request.getNote());
        emotion.setDate(LocalDate.now());
        
        emotionLogRepository.save(emotion);
        return ResponseEntity.ok("감정이 저장되었습니다!");
    }

    @PostMapping("/workout")
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

    private User getCurrentUser(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String oauthId = String.valueOf(oAuth2User.getAttributes().get("id"));
        String userIdentifier = email != null ? email : oauthId;
        
        return userRepository.findByEmail(userIdentifier)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    public static class MealRequest {
        private String description;
        private Integer caloriesEstimate;
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getCaloriesEstimate() { return caloriesEstimate; }
        public void setCaloriesEstimate(Integer caloriesEstimate) { this.caloriesEstimate = caloriesEstimate; }
    }

    public static class EmotionRequest {
        private String mood;
        private String note;
        
        public String getMood() { return mood; }
        public void setMood(String mood) { this.mood = mood; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
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