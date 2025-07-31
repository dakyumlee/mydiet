package com.mydiet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String nickname;
    private String email;
    private String password;
    private Double weightGoal;
    private String emotionMode;
}
