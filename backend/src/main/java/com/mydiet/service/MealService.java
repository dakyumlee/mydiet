package com.mydiet.service;

import com.mydiet.dto.MealRequest;
import com.mydiet.model.MealLog;
import com.mydiet.model.User;
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
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        MealLog mealLog = MealLog.builder()
            .user(user)
            .description(request.getDescription())
            .photoUrl(request.getPhotoUrl())
            .caloriesEstimate(request.getCaloriesEstimate())
            .date(request.getDate() != null ? request.getDate() : LocalDate.now())
            .build();

        return mealLogRepository.save(mealLog);
    }

    public List<MealLog> getTodayMeals(Long userId) {
        return mealLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
}
