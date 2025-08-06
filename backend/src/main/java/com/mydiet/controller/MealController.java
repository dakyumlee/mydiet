package com.mydiet.controller;

import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {
    
    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> saveMeal(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null && request.containsKey("userId")) {
                userId = Long.valueOf(request.get("userId").toString());
            }
            if (userId == null) {
                userId = 1L;
            }
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            MealLog meal = new MealLog();
            meal.setUser(user);
            meal.setDescription((String) request.get("description"));
            
            Object caloriesObj = request.get("calories");
            if (caloriesObj != null) {
                meal.setCaloriesEstimate(Integer.valueOf(caloriesObj.toString()));
            }
             
            if (request.containsKey("photoUrl")) {
                meal.setPhotoUrl((String) request.get("photoUrl"));
            }
             
            if (request.containsKey("photoData")) {
                String photoData = (String) request.get("photoData");
                meal.setPhotoUrl(photoData);
            }
            
            meal.setDate(LocalDate.now());
            
            MealLog saved = mealLogRepository.save(meal);
            log.info("Meal saved for user {}: {}", user.getEmail(), saved.getDescription());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", saved.getId());
            response.put("message", "식사가 기록되었습니다!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error saving meal: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadMealWithPhoto(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("calories") Integer calories,
                                                 HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) userId = 1L;
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String dataUrl = "data:" + file.getContentType() + ";base64," + base64Image;
            
            MealLog meal = new MealLog();
            meal.setUser(user);
            meal.setDescription(description);
            meal.setCaloriesEstimate(calories);
            meal.setPhotoUrl(dataUrl);
            meal.setDate(LocalDate.now());
            
            MealLog saved = mealLogRepository.save(meal);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", saved.getId());
            response.put("message", "사진과 함께 저장되었습니다!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading meal with photo: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(@RequestParam(required = false) Long userId, 
                                          HttpSession session) {
        try {
            if (userId == null) {
                userId = (Long) session.getAttribute("userId");
            }
            if (userId == null) userId = 1L;
            
            List<MealLog> meals = mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
            return ResponseEntity.ok(meals);
        } catch (Exception e) {
            log.error("Error fetching meals: ", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}