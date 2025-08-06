package com.mydiet.controller;

import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.repository.MealLogRepository;
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
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {
    
    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> saveMeal(@AuthenticationPrincipal OAuth2UserPrincipal principal,
                                      @RequestBody Map<String, Object> request) {
        try {
            User user = userRepository.findById(principal.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            MealLog meal = new MealLog();
            meal.setUser(user);
            meal.setDescription((String) request.get("description"));
            meal.setCaloriesEstimate((Integer) request.get("calories"));
            meal.setDate(LocalDate.now());
            
            MealLog saved = mealLogRepository.save(meal);
            log.info("Meal saved for user {}: {}", user.getEmail(), saved.getDescription());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id", saved.getId(),
                "message", "식사 기록이 저장되었습니다."
            ));
        } catch (Exception e) {
            log.error("Error saving meal: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(@AuthenticationPrincipal OAuth2UserPrincipal principal) {
        try {
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(
                principal.getUserId(), 
                LocalDate.now()
            );
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            log.error("Error fetching meals: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}