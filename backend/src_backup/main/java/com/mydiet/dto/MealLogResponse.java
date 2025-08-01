package com.mydiet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealLogResponse {
    private Long id;
    private String userNickname;
    private String userEmail;
    private String description;
    private Integer caloriesEstimate;
    private LocalDate date;
}
