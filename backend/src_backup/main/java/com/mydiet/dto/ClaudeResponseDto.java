package com.mydiet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaudeResponseDto {
    private Long id;
    private String userNickname;
    private String userEmail;
    private String type;
    private String content;
    private LocalDateTime createdAt;
}
