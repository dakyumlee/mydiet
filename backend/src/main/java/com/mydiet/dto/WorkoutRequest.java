package com.mydiet.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkoutRequest {
    private Long userId;
    private String type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDate date;
}