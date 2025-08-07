package com.mydiet.controller;

import com.mydiet.dto.MealRequest;
import com.mydiet.model.MealLog;
import com.mydiet.service.MealService;
import com.mydiet.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final SessionUtil sessionUtil;

    @PostMapping
    public ResponseEntity<?> saveMeal(@RequestBody MealRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = sessionUtil.getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            request.setUserId(userId);
            MealLog saved = mealService.saveMeal(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("식사 기록 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(HttpServletRequest httpRequest) {
        try {
            Long userId = sessionUtil.getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            return ResponseEntity.ok(mealService.getTodayMeals(userId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("식사 기록 조회 실패: " + e.getMessage());
        }
    }
}