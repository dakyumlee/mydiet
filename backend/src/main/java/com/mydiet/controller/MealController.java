package com.mydiet.controller;

import com.mydiet.dto.MealRequest;
import com.mydiet.model.MealLog;
import com.mydiet.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Slf4j
public class MealController {

    private final MealService mealService;

    @PostMapping
    public ResponseEntity<MealLog> saveMeal(@Valid @RequestBody MealRequest request) {
        log.info("식단 기록 저장 요청: {}", request);
        MealLog saved = mealService.saveMeal(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/today")
    public ResponseEntity<List<MealLog>> getTodayMeals(@RequestParam Long userId) {
        log.info("오늘 식단 조회 - userId: {}", userId);
        List<MealLog> meals = mealService.getTodayMeals(userId);
        return ResponseEntity.ok(meals);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealLog>> getUserMeals(@PathVariable Long userId) {
        List<MealLog> meals = mealService.getAllUserMeals(userId);
        return ResponseEntity.ok(meals);
    }
}