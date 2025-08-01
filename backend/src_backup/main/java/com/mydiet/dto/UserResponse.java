package com.mydiet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.mydiet.model.User;

@Data
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String nickname;
    private String email;
    private Double weightGoal;
    private String emotionMode;
    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.weightGoal = user.getWeightGoal();
        this.emotionMode = user.getEmotionMode();
        this.createdAt = user.getCreatedAt();
    }
}