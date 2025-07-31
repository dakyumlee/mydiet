package com.mydiet.controller;

import com.mydiet.model.MealLog;
import com.mydiet.repository.MealLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MealLogController {

    private final MealLogRepository mealLogRepository;

    @PostMapping
    public ResponseEntity<?> createMealLog(@RequestBody MealLog mealLog) {
        mealLog.setCreatedAt(LocalDateTime.now());
        mealLogRepository.save(mealLog);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealLog>> getUserMeals(@PathVariable Long userId) {
        List<MealLog> meals = mealLogRepository.findAllByUserId(userId);
        return ResponseEntity.ok(meals);
    }
}
