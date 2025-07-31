package com.mydiet.controller;

import com.mydiet.dto.AdminSummaryResponse;
import com.mydiet.model.User;
import com.mydiet.model.MealLog;
import com.mydiet.model.ClaudeResponse;
import com.mydiet.repository.UserRepository;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.ClaudeResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final ClaudeResponseRepository claudeResponseRepository;

    @GetMapping("/summary")
    public AdminSummaryResponse getSummary() {
        long userCount = userRepository.count();
        long todayMeals = mealLogRepository.countByCreatedAt(LocalDate.now());
        long todayClaude = claudeResponseRepository.countByCreatedAt(LocalDate.now());
        long activeUsers = userRepository.countByLastLoginAfter(LocalDate.now().minusDays(7));

        return new AdminSummaryResponse(userCount, todayMeals, todayClaude, activeUsers);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findTop30ByOrderByCreatedAtDesc();
    }

    @GetMapping("/meals")
    public List<MealLog> getAllMeals() {
        return mealLogRepository.findAll();
    }

    @GetMapping("/claude")
    public List<ClaudeResponse> getAllClaudeResponses() {
        return claudeResponseRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
