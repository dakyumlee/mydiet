package com.mydiet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class MealRequest {
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "음식 설명은 필수입니다")
    private String description;
    
    private Integer caloriesEstimate;
    private String photoUrl;
}