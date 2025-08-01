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
public class ActiveUserResponse {
    private Long userId;
    private String nickname;
    private String email;
    private LocalDate lastActivity;
    private String activityType;
    private int activityCount;
}
