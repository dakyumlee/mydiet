package com.mydiet.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import com.mydiet.model.WeightLog;
import com.mydiet.model.MealLog;
import com.mydiet.model.EmotionLog;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail {
    private com.mydiet.model.User user;
    private List<WeightLog> recentWeights;
    private List<MealLog> recentMeals;
    private List<EmotionLog> recentEmotions;
}