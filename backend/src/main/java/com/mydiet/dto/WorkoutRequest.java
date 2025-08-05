package com.mydiet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

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