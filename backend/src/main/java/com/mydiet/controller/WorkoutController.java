package com.mydiet.controller;

import com.mydiet.dto.WorkoutRequest;
import com.mydiet.model.WorkoutLog;
import com.mydiet.service.WorkoutService;
import com.mydiet.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;
    private final SessionUtil sessionUtil;

    @PostMapping
    public ResponseEntity<?> saveWorkout(@RequestBody WorkoutRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = sessionUtil.getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            request.setUserId(userId);
            WorkoutLog saved = workoutService.saveWorkout(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("운동 기록 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts(HttpServletRequest httpRequest) {
        try {
            Long userId = sessionUtil.getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            return ResponseEntity.ok(workoutService.getTodayWorkouts(userId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("운동 기록 조회 실패: " + e.getMessage());
        }
    }
}