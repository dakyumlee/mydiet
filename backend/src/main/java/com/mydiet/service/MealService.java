package com.mydiet.service;

import com.mydiet.model.MealLog;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;

    public MealLog saveMeal(MealRequest request) {
        MealLog mealLog = new MealLog();
        mealLog.setUser(userRepository.findById(request.getUserId()).orElseThrow());
        mealLog.setDescription(request.getDescription());
        mealLog.setCaloriesEstimate(request.getCaloriesEstimate());
        mealLog.setPhotoUrl(request.getPhotoUrl());
        mealLog.setDate(LocalDate.now());
        
        return mealLogRepository.save(mealLog);
    }

    public List<MealLog> getTodayMeals(Long userId) {
        return mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    public static class MealRequest {
        private Long userId;
        private String description;
        private Integer caloriesEstimate;
        private String photoUrl;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getCaloriesEstimate() { return caloriesEstimate; }
        public void setCaloriesEstimate(Integer caloriesEstimate) { this.caloriesEstimate = caloriesEstimate; }
        
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    }
}