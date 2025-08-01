package com.mydiet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MYDIET_USER")
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long id;
    
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;
    
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    
    @Column(name = "NICKNAME")
    private String nickname;
    
    @Column(name = "WEIGHT_GOAL")
    private Double weightGoal;
    
    @Column(name = "TARGET_CALORIES")
    private Double targetCalories;
    
    @Column(name = "EMOTION_MODE")
    private String emotionMode;
    
    @Column(name = "USER_ROLE")
    private String role = "USER";
    
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public Double getWeightGoal() { return weightGoal; }
    public void setWeightGoal(Double weightGoal) { this.weightGoal = weightGoal; }
    
    public Double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Double targetCalories) { this.targetCalories = targetCalories; }
    
    public String getEmotionMode() { return emotionMode; }
    public void setEmotionMode(String emotionMode) { this.emotionMode = emotionMode; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
