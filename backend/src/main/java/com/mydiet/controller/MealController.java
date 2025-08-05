package com.mydiet.controller;

import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> saveMeal(@RequestBody MealRequest request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        MealLog meal = new MealLog();
        meal.setUser(user);
        meal.setDescription(request.getDescription());
        meal.setCaloriesEstimate(request.getCaloriesEstimate());
        meal.setPhotoUrl(request.getPhotoUrl());
        meal.setDate(LocalDate.now());
        
        mealLogRepository.save(meal);
        return ResponseEntity.ok("식단이 저장되었습니다!");
    }

    @GetMapping("/today")
    public ResponseEntity<List<MealLog>> getTodayMeals(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<MealLog> meals = mealLogRepository.findByUserIdAndDate(user.getId(), LocalDate.now());
        return ResponseEntity.ok(meals);
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
        private String photoUrl;
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getCaloriesEstimate() { return caloriesEstimate; }
        public void setCaloriesEstimate(Integer caloriesEstimate) { this.caloriesEstimate = caloriesEstimate; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    }
}