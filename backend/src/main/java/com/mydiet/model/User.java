package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    
    private String nickname;
    private Double weightGoal;
    private String emotionMode; // 예: 무자비, 츤데레, 다정함
    
    private String socialId;
    private String socialProvider;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}