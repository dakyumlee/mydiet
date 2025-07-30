package com.mydiet.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import com.mydiet.model.WeightLog;
import com.mydiet.model.MealLog;
import com.mydiet.model.EmotionLog;
import com.mydiet.model.DiaryLog;
import com.mydiet.model.WorkoutLog;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    private Long userId;
    private List<WeightLog> todayWeights;
    private List<MealLog> todayMeals;
    private List<EmotionLog> todayEmotions;
    private List<DiaryLog> todayDiary;
    private List<WorkoutLog> todayWorkouts;
}