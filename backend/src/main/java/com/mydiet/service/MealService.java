package com.mydiet.service;

import com.mydiet.dto.MealRequest;
import com.mydiet.model.MealLog;
import com.mydiet.model.User;
import com.mydiet.repository.MealLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j 
public class MealService {
    
    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public MealLog saveMeal(MealRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        MealLog mealLog = new MealLog();
        mealLog.setUser(user);
        mealLog.setDescription(request.getDescription());
        mealLog.setCaloriesEstimate(request.getCaloriesEstimate());
        mealLog.setPhotoUrl(request.getPhotoUrl());
        mealLog.setDate(LocalDate.now());
        
        MealLog saved = mealLogRepository.save(mealLog);
        log.info("식단 기록 저장 완료 - id: {}, user: {}", saved.getId(), user.getNickname());
        
        return saved;
    }
    
    public List<MealLog> getTodayMeals(Long userId) {
        return mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
    
    public List<MealLog> getAllUserMeals(Long userId) {
        return mealLogRepository.findByUserIdOrderByDateDesc(userId);
    }
}