package com.mydiet.controller;

import com.mydiet.model.MealLog;
import com.mydiet.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @PostMapping
    public ResponseEntity<?> saveMeal(@RequestBody MealService.MealRequest request, Authentication authentication) {
        try { 
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Authentication required");
            }
             
            Long actualUserId = request.getUserId() != null ? request.getUserId() : 1L;
            request.setUserId(actualUserId);
            
            MealLog saved = mealService.saveMeal(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving meal: " + e.getMessage());
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(@RequestParam(required = false) Long userId, 
                                         Authentication authentication) {
        try { 
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(Collections.emptyList());  
            }
             
            Long actualUserId = userId != null ? userId : 1L;
            
            return ResponseEntity.ok(mealService.getTodayMeals(actualUserId));
        } catch (Exception e) { 
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}