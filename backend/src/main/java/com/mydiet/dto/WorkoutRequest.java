package com.mydiet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {
    private Long userId;
    private String type;
    private Integer duration;
    private Integer caloriesBurned;
}