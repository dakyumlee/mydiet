package com.mydiet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealRequest {
    private Long userId;
    private String description;
    private String photoUrl;
    private Integer caloriesEstimate;
}