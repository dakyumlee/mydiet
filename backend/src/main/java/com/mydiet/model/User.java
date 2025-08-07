package com.mydiet.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password; // 일반 로그인용
    
    private String provider; // oauth2 제공자 (google, kakao 등)
    private String providerId; // oauth2 제공자의 사용자 ID

    @Column(name = "weight_goal")
    private Double weightGoal;
    
    @Column(name = "emotion_mode")
    private String emotionMode; // 예: 무자비, 츤데레, 다정함
    
    @Builder.Default
    private String role = "USER"; // USER, ADMIN

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}