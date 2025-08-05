package com.mydiet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRequest {
    private Long userId;
    private String mood;
    private String note;
    private LocalDate date;
}