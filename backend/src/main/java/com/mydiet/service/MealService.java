package com.mydiet.service;

import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import com.mydiet.dto.MealRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MealService {

    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;

    public MealLog saveMeal(MealRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        MealLog mealLog = new MealLog();
        mealLog.setUser(user);
        mealLog.setDescription(request.getDescription());
        mealLog.setCaloriesEstimate(request.getCaloriesEstimate());
        mealLog.setPhotoUrl(request.getPhotoUrl());
        mealLog.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());

        return mealLogRepository.save(mealLog);
    }

    @Transactional(readOnly = true)
    public List<MealLog> getTodayMeals(Long userId) {
        return mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<MealLog> getMealsByDate(Long userId, LocalDate date) {
        return mealLogRepository.findByUserIdAndDate(userId, date);
    }

    @Transactional(readOnly = true)
    public List<MealLog> getAllUserMeals(Long userId) {

        return mealLogRepository.findAll().stream()
                .filter(meal -> meal.getUser().getId().equals(userId))
                .collect(java.util.stream.Collectors.toList());
    }
}