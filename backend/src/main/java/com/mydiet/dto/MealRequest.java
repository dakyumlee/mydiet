package com.mydiet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealRequest {
    private Long userId;
    private String description;
    private String photoUrl;
    private Integer caloriesEstimate;
    private LocalDate date;
}