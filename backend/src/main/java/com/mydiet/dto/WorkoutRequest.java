package com.mydiet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {
    private Long userId;
    private String type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDate date;
}
