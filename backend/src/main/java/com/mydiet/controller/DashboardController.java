package com.mydiet.controller;

import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.WorkoutLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final MealLogRepository mealRepo;
    private final WorkoutLogRepository workoutRepo;
    private final EmotionLogRepository emotionRepo;
    private final UserRepository userRepo;

    @GetMapping("/summary/{userId}")
    public Map<String, Object> getSummary(@PathVariable Long userId) {
        long mealCount = mealRepo.countByUserId(userId);
        long workoutCount = workoutRepo.countByUserId(userId);
        long emotionCount = emotionRepo.countByUserId(userId);
        double weight = userRepo.findById(userId).map(User::getWeightGoal).orElse(0.0);

        return Map.of(
                "mealCount", mealCount,
                "workoutCount", workoutCount,
                "emotionCount", emotionCount,
                "userWeight", weight
        );
    }
}
