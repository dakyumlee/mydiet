package com.mydiet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealRequest {
    private Long userId;
    private String description;
    private Integer caloriesEstimate;
    private String photoUrl;
    private LocalDate date;
}