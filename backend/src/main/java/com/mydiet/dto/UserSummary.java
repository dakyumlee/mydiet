package com.mydiet.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String name;
    private String email;
    private Double currentWeight;
    private Double targetWeight;
    private Integer age;
    private Integer height;
    private LocalDateTime createdAt;
}