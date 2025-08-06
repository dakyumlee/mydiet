package com.mydiet.controller;

import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.repository.MealLogRepository;
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
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MealController {
    
    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> saveMeal(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("Saving meal: {}", request);
        
        try {
            // 사용자 ID 가져오기
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                // 세션에 없으면 기본값 사용
                userId = 1L;
                log.info("Using default userId: 1");
            }
            
            // 사용자가 없으면 생성

            final Long finalUserId = userId;
            
            User user = userRepository.findById(userId).orElseGet(() -> {
                User newUser = new User();
                newUser.setId(userId);
                newUser.setEmail("user" + userId + "@mydiet.com");
                newUser.setNickname("사용자" + userId);
                return userRepository.save(newUser);
            });
            
            // MealLog 생성
            MealLog meal = new MealLog();
            meal.setUser(user);
            meal.setDescription((String) request.get("description"));
            meal.setCaloriesEstimate(Integer.valueOf(request.getOrDefault("calories", 0).toString()));
            
            // 사진 처리
            if (request.containsKey("photoData")) {
                meal.setPhotoUrl((String) request.get("photoData"));
            }
            
            meal.setDate(LocalDate.now());
            
            MealLog saved = mealLogRepository.save(meal);
            log.info("Meal saved with id: {}", saved.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", saved.getId());
            response.put("message", "식단이 저장되었습니다!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error saving meal: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            log.error("Error fetching meals: ", e);
            return ResponseEntity.ok(List.of());
        }
    }
}