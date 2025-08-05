package com.mydiet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRequest {
    private Long userId;
    private String mood;
    private String note;
    private LocalDate date;
}