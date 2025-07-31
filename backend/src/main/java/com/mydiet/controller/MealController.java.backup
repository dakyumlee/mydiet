package com.mydiet.controller;

import com.mydiet.dto.MealRequest;
import com.mydiet.entity.MealLog;
import com.mydiet.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    @PostMapping
    public ResponseEntity<?> saveMeal(@RequestBody MealRequest request) {
        MealLog saved = mealService.saveMeal(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(@RequestParam Long userId) {
        return ResponseEntity.ok(mealService.getTodayMeals(userId));
    }
}