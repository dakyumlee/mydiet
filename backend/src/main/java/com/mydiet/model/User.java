package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true)
    private String email;

    @Column(name = "weight_goal")
    private Double weightGoal;

    @Column(name = "emotion_mode")
    private String emotionMode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // OAuth 관련 필드들
    @Column(name = "provider")
    private String provider; // "google", "kakao", "local"

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "role")
    private String role = "USER"; // "USER", "ADMIN"

    // 연관 관계 (선택사항 - 성능상 필요 없으면 제거 가능)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MealLog> meals;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutLog> workouts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmotionLog> emotions;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (role == null) {
            role = "USER";
        }
        if (emotionMode == null) {
            emotionMode = "다정함";
        }
    }

    // 편의 메서드
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public void setAsAdmin() {
        this.role = "ADMIN";
    }
}